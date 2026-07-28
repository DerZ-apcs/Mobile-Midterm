package com.example.midterm_application.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.CheckoutSummary;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderItem;
import com.example.midterm_application.data.model.PromoCode;
import com.example.midterm_application.data.model.RewardProduct;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;
import com.example.midterm_application.data.repository.PromoRepository;
import com.example.midterm_application.utils.CheckoutPriceCalculator;
import com.example.midterm_application.utils.RewardCalculator;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class EnhancedCheckoutIntegrationFlowTest {
    private static final double DELTA = 0.001;

    @Test
    public void fullProfileToRewardsFlowPreservesCheckoutStateAndRewards() {
        FlowState state = new FlowState("10 Profile Street");
        state.rewardState.setStampCount(8);
        state.cart.add(customizedCartItem());

        CheckoutSummary preview = state.preview("CODECUP10", true);
        assertEquals(20.00, state.cartTotal(), DELTA);
        assertEquals(20.00, preview.getSubtotal(), DELTA);
        assertEquals(5.00, preview.getLoyaltyDiscount(), DELTA);
        assertEquals(1.50, preview.getPromoDiscount(), DELTA);
        assertEquals(13.50, preview.getFinalTotal(), DELTA);

        Order order = state.placeOrder("10 Profile Street", " codecup10 ", true);
        assertEquals(0, state.rewardState.getStampCount());
        assertTrue(state.cart.isEmpty());
        assertEquals("10 Profile Street", order.getDeliveryAddress());
        assertEquals(20.00, order.getSubtotal(), DELTA);
        assertEquals("CODECUP10", order.getPromoCode());
        assertEquals(5.00, order.getLoyaltyDiscount(), DELTA);
        assertEquals(1.50, order.getPromoDiscount(), DELTA);
        assertEquals(13.50, order.getFinalTotal(), DELTA);
        assertEquals(13.50, order.getTotalPrice(), DELTA);
        assertTrue(order.isLoyaltyRewardUsed());

        state.profileAddress = "99 New Profile Street";
        assertEquals("10 Profile Street", state.orders.get(0).getDeliveryAddress());

        OrderItem orderItem = state.orderItems.get(0);
        assertEquals("Americano", orderItem.getCoffeeName());
        assertEquals("DOUBLE", orderItem.getShot());
        assertEquals("LARGE", orderItem.getSize());
        assertEquals("LESS_ICE", orderItem.getIce());
        assertEquals(4, orderItem.getQuantity());
        assertEquals(5.00, orderItem.getUnitPrice(), DELTA);
        assertEquals(20.00, orderItem.getTotalPrice(), DELTA);
        assertEquals("No sugar", orderItem.getNote());

        assertTrue(state.orderHistoryValid());
        assertTrue(state.completeOrder(order.getId()));
        assertEquals(Order.STATUS_COMPLETED, order.getStatus());
        assertEquals(1, state.rewardState.getStampCount());
        assertEquals(135, state.rewardState.getTotalPoints());
        assertEquals(1, state.countRewardTransactions(RewardTransaction.TYPE_EARN));
        assertEquals(135, state.rewardTransactions.get(0).getPoints());

        RewardProduct rewardProduct = new RewardProduct(1, "Free Mocha", 0, 120);
        assertTrue(state.redeem(rewardProduct));
        assertEquals(15, state.rewardState.getTotalPoints());
        assertEquals(1, state.rewardState.getStampCount());
        assertEquals(1, state.countRewardTransactions(RewardTransaction.TYPE_REDEEM));
        assertTrue(state.rewardHistoryValid());

        FlowState restarted = state.restartCopy();
        assertEquals(1, restarted.orders.size());
        assertEquals("10 Profile Street", restarted.orders.get(0).getDeliveryAddress());
        assertEquals(Order.STATUS_COMPLETED, restarted.orders.get(0).getStatus());
        assertEquals(13.50, restarted.orders.get(0).getFinalTotal(), DELTA);
        assertEquals(15, restarted.rewardState.getTotalPoints());
        assertEquals(1, restarted.rewardState.getStampCount());
        assertEquals(2, restarted.rewardTransactions.size());
    }

    @Test
    public void selectingLoyaltyThenBackingOutDoesNotConsumeStamps() {
        FlowState state = new FlowState("10 Profile Street");
        state.rewardState.setStampCount(8);
        state.cart.add(customizedCartItem());

        CheckoutSummary preview = state.preview("", true);
        assertEquals(5.00, preview.getLoyaltyDiscount(), DELTA);

        state.cancelCheckout();

        assertEquals(8, state.rewardState.getStampCount());
        assertEquals(1, state.cart.size());
        assertTrue(state.orders.isEmpty());
    }

    @Test
    public void failedPlaceOrderPreservesCartAndStamps() {
        FlowState state = new FlowState("10 Profile Street");
        state.rewardState.setStampCount(8);
        state.cart.add(customizedCartItem());

        assertFalse(state.tryPlaceOrder("", "CODECUP10", true));
        assertEquals(8, state.rewardState.getStampCount());
        assertEquals(1, state.cart.size());
        assertTrue(state.orders.isEmpty());

        assertFalse(state.tryPlaceOrder("10 Profile Street", "BADCODE", true));
        assertEquals(8, state.rewardState.getStampCount());
        assertEquals(1, state.cart.size());
        assertTrue(state.orders.isEmpty());
    }

    private static CartItem customizedCartItem() {
        return new CartItem(7, "Americano", 101, "DOUBLE", "LARGE", "LESS_ICE",
                4, 5.00, 20.00, "No sugar");
    }

    private static class FlowState {
        private final PromoRepository promoRepository = new PromoRepository();
        private String profileAddress;
        private long nextOrderId = 1;
        private final List<CartItem> cart = new ArrayList<>();
        private final List<Order> orders = new ArrayList<>();
        private final List<OrderItem> orderItems = new ArrayList<>();
        private final List<RewardTransaction> rewardTransactions = new ArrayList<>();
        private final RewardState rewardState = RewardState.initial();

        FlowState(String profileAddress) {
            this.profileAddress = profileAddress;
        }

        double cartTotal() {
            double total = 0.00;
            for (CartItem item : cart) {
                total += item.getTotalPrice();
            }
            return total;
        }

        CheckoutSummary preview(String rawPromoCode, boolean loyaltyRequested) {
            String normalizedCode = promoRepository.normalizeCode(rawPromoCode);
            PromoCode promoCode = promoRepository.findPromoCode(normalizedCode);
            return CheckoutPriceCalculator.calculate(cart, promoCode, normalizedCode,
                    loyaltyRequested, RewardCalculator.canClaimStampCard(rewardState.getStampCount()));
        }

        Order placeOrder(String deliveryAddress, String rawPromoCode, boolean loyaltyRequested) {
            if (!tryPlaceOrder(deliveryAddress, rawPromoCode, loyaltyRequested)) {
                throw new AssertionError("Place order failed");
            }
            return orders.get(orders.size() - 1);
        }

        boolean tryPlaceOrder(String deliveryAddress, String rawPromoCode, boolean loyaltyRequested) {
            if (cart.isEmpty() || deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
                return false;
            }
            boolean loyaltyAvailable = RewardCalculator.canClaimStampCard(rewardState.getStampCount());
            if (loyaltyRequested && !loyaltyAvailable) {
                return false;
            }
            String normalizedCode = promoRepository.normalizeCode(rawPromoCode);
            PromoCode promoCode = promoRepository.findPromoCode(normalizedCode);
            CheckoutSummary summary = CheckoutPriceCalculator.calculate(cart, promoCode, normalizedCode,
                    loyaltyRequested, loyaltyAvailable);
            if (!normalizedCode.isEmpty() && !summary.isPromoAccepted()) {
                return false;
            }

            long orderId = nextOrderId++;
            Order order = new Order(1000L, summary.getSubtotal(), summary.getPromoCode(),
                    summary.getPromoDiscount(), summary.getLoyaltyDiscount(), summary.getFinalTotal(),
                    deliveryAddress.trim(), loyaltyRequested);
            order.setId(orderId);
            orders.add(order);
            for (CartItem item : cart) {
                orderItems.add(new OrderItem(orderId, item.getCoffeeId(), item.getCoffeeName(),
                        item.getImageResId(), item.getShot(), item.getSize(), item.getIce(),
                        item.getQuantity(), item.getUnitPrice(), item.getTotalPrice(), item.getNote()));
            }
            if (loyaltyRequested) {
                rewardState.setStampCount(RewardCalculator.calculateStampCountAfterClaim(rewardState.getStampCount()));
            }
            cart.clear();
            return true;
        }

        void cancelCheckout() {
            // No persisted state changes should happen before Place Order succeeds.
        }

        boolean completeOrder(long orderId) {
            for (Order order : orders) {
                if (order.getId() == orderId && Order.STATUS_ONGOING.equals(order.getStatus())) {
                    order.setStatus(Order.STATUS_COMPLETED);
                    int earnedPoints = RewardCalculator.calculateEarnedPoints(order.getTotalPrice());
                    rewardState.setTotalPoints(RewardCalculator.calculateTotalPointsAfterEarn(
                            rewardState.getTotalPoints(), earnedPoints));
                    rewardState.setStampCount(RewardCalculator.calculateStampCountAfterEarn(
                            rewardState.getStampCount()));
                    rewardTransactions.add(new RewardTransaction(orderId, 2000L,
                            RewardTransaction.TYPE_EARN, earnedPoints,
                            "Order #" + orderId + " completed"));
                    return true;
                }
            }
            return false;
        }

        boolean redeem(RewardProduct product) {
            if (!RewardCalculator.canRedeem(rewardState.getTotalPoints(), product.getPointCost())) {
                return false;
            }
            rewardState.setTotalPoints(RewardCalculator.calculateTotalPointsAfterRedeem(
                    rewardState.getTotalPoints(), product.getPointCost()));
            rewardTransactions.add(new RewardTransaction(null, 3000L,
                    RewardTransaction.TYPE_REDEEM, -product.getPointCost(), product.getName()));
            return true;
        }

        boolean orderHistoryValid() {
            return !orders.isEmpty() && !orderItems.isEmpty();
        }

        boolean rewardHistoryValid() {
            return !rewardTransactions.isEmpty();
        }

        int countRewardTransactions(String type) {
            int count = 0;
            for (RewardTransaction transaction : rewardTransactions) {
                if (type.equals(transaction.getType())) {
                    count++;
                }
            }
            return count;
        }

        FlowState restartCopy() {
            FlowState copy = new FlowState(profileAddress);
            copy.nextOrderId = nextOrderId;
            copy.orders.addAll(orders);
            copy.orderItems.addAll(orderItems);
            copy.rewardTransactions.addAll(rewardTransactions);
            copy.rewardState.setStampCount(rewardState.getStampCount());
            copy.rewardState.setTotalPoints(rewardState.getTotalPoints());
            return copy;
        }
    }
}

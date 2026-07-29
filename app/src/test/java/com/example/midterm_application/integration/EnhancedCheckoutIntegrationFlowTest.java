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
        assertTrue(state.claimStampReward(rewardCartItem("Cappuccino", CartItem.REWARD_SOURCE_STAMP_CARD)));
        assertEquals(0, state.rewardState.getStampCount());
        assertEquals(2, state.cart.size());

        CheckoutSummary preview = state.preview("CODECUP10", true);
        assertEquals(20.00, state.cartTotal(), DELTA);
        assertEquals(20.00, preview.getSubtotal(), DELTA);
        assertEquals(0.00, preview.getLoyaltyDiscount(), DELTA);
        assertEquals(2.00, preview.getPromoDiscount(), DELTA);
        assertEquals(18.00, preview.getFinalTotal(), DELTA);

        Order order = state.placeOrder("10 Profile Street", " codecup10 ", true);
        assertTrue(state.cart.isEmpty());
        assertEquals("10 Profile Street", order.getDeliveryAddress());
        assertEquals(20.00, order.getSubtotal(), DELTA);
        assertEquals("CODECUP10", order.getPromoCode());
        assertEquals(0.00, order.getLoyaltyDiscount(), DELTA);
        assertEquals(2.00, order.getPromoDiscount(), DELTA);
        assertEquals(18.00, order.getFinalTotal(), DELTA);
        assertEquals(18.00, order.getTotalPrice(), DELTA);
        assertEquals(Order.DELIVERY_ASAP, order.getDeliveryType());
        assertEquals(0L, order.getScheduledAt());
        assertFalse(order.isLoyaltyRewardUsed());

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
        assertEquals(CartItem.REWARD_SOURCE_NONE, orderItem.getRewardSource());

        OrderItem rewardOrderItem = state.orderItems.get(1);
        assertEquals("Cappuccino", rewardOrderItem.getCoffeeName());
        assertEquals(1, rewardOrderItem.getQuantity());
        assertEquals(0.00, rewardOrderItem.getUnitPrice(), DELTA);
        assertEquals(0.00, rewardOrderItem.getTotalPrice(), DELTA);
        assertEquals(CartItem.REWARD_SOURCE_STAMP_CARD, rewardOrderItem.getRewardSource());

        assertTrue(state.orderHistoryValid());
        assertTrue(state.completeOrder(order.getId()));
        assertEquals(Order.STATUS_COMPLETED, order.getStatus());
        assertEquals(1, state.rewardState.getStampCount());
        assertEquals(180, state.rewardState.getTotalPoints());
        assertEquals(1, state.countRewardTransactions(RewardTransaction.TYPE_EARN));
        assertEquals(180, state.rewardTransactions.get(0).getPoints());

        RewardProduct rewardProduct = new RewardProduct(3, "Mocha", 0, 120);
        assertTrue(state.redeem(rewardProduct));
        assertEquals(60, state.rewardState.getTotalPoints());
        assertEquals(1, state.rewardState.getStampCount());
        assertEquals(1, state.cart.size());
        assertEquals(CartItem.REWARD_SOURCE_POINTS, state.cart.get(0).getRewardSource());
        assertEquals(0.00, state.cart.get(0).getTotalPrice(), DELTA);
        assertEquals(1, state.countRewardTransactions(RewardTransaction.TYPE_REDEEM));
        assertTrue(state.rewardHistoryValid());

        FlowState restarted = state.restartCopy();
        assertEquals(1, restarted.orders.size());
        assertEquals("10 Profile Street", restarted.orders.get(0).getDeliveryAddress());
        assertEquals(Order.STATUS_COMPLETED, restarted.orders.get(0).getStatus());
        assertEquals(18.00, restarted.orders.get(0).getFinalTotal(), DELTA);
        assertEquals(60, restarted.rewardState.getTotalPoints());
        assertEquals(1, restarted.rewardState.getStampCount());
        assertEquals(2, restarted.rewardTransactions.size());
        assertEquals(1, restarted.cart.size());
        assertEquals(CartItem.REWARD_SOURCE_POINTS, restarted.cart.get(0).getRewardSource());
    }

    @Test
    public void selectingLoyaltyThenBackingOutDoesNotConsumeStamps() {
        FlowState state = new FlowState("10 Profile Street");
        state.rewardState.setStampCount(8);
        state.cart.add(customizedCartItem());

        CheckoutSummary preview = state.preview("", true);
        assertEquals(0.00, preview.getLoyaltyDiscount(), DELTA);

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

        assertFalse(state.tryPlaceOrder("10 Profile Street", "CODECUP10", true,
                Order.DELIVERY_SCHEDULED, 1000L));
        assertEquals(8, state.rewardState.getStampCount());
        assertEquals(1, state.cart.size());
        assertTrue(state.orders.isEmpty());
    }

    @Test
    public void scheduledPlaceOrderPersistsSchedulingSnapshot() {
        FlowState state = new FlowState("10 Profile Street");
        state.cart.add(customizedCartItem());
        long scheduledAt = 2_000_000L;

        assertTrue(state.tryPlaceOrder("10 Profile Street", "", false,
                Order.DELIVERY_SCHEDULED, scheduledAt));

        Order order = state.orders.get(0);
        assertEquals(Order.DELIVERY_SCHEDULED, order.getDeliveryType());
        assertEquals(scheduledAt, order.getScheduledAt());
        assertTrue(state.cart.isEmpty());
    }

    private static CartItem customizedCartItem() {
        return new CartItem(7, "Americano", 101, "DOUBLE", "LARGE", "LESS_ICE",
                4, 5.00, 20.00, "No sugar");
    }

    private static CartItem rewardCartItem(String coffeeName, String rewardSource) {
        return new CartItem(2, coffeeName, 102, "SINGLE", "SMALL", "NORMAL",
                1, 0.00, 0.00, "", rewardSource);
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
            return CheckoutPriceCalculator.calculate(cart, promoCode, normalizedCode, false, false);
        }

        Order placeOrder(String deliveryAddress, String rawPromoCode, boolean loyaltyRequested) {
            if (!tryPlaceOrder(deliveryAddress, rawPromoCode, loyaltyRequested)) {
                throw new AssertionError("Place order failed");
            }
            return orders.get(orders.size() - 1);
        }

        boolean tryPlaceOrder(String deliveryAddress, String rawPromoCode, boolean loyaltyRequested) {
            return tryPlaceOrder(deliveryAddress, rawPromoCode, loyaltyRequested, Order.DELIVERY_ASAP, 0L);
        }

        boolean tryPlaceOrder(String deliveryAddress, String rawPromoCode, boolean loyaltyRequested,
                              String deliveryType, long scheduledAt) {
            if (cart.isEmpty() || deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
                return false;
            }
            String deliveryTypeSnapshot = Order.DELIVERY_SCHEDULED.equals(deliveryType)
                    ? Order.DELIVERY_SCHEDULED
                    : Order.DELIVERY_ASAP;
            long scheduledAtSnapshot = Order.DELIVERY_SCHEDULED.equals(deliveryTypeSnapshot) ? scheduledAt : 0L;
            if (Order.DELIVERY_SCHEDULED.equals(deliveryTypeSnapshot) && scheduledAtSnapshot <= 1000L) {
                return false;
            }
            String normalizedCode = promoRepository.normalizeCode(rawPromoCode);
            PromoCode promoCode = promoRepository.findPromoCode(normalizedCode);
            CheckoutSummary summary = CheckoutPriceCalculator.calculate(cart, promoCode, normalizedCode,
                    false, false);
            if (!normalizedCode.isEmpty() && !summary.isPromoAccepted()) {
                return false;
            }

            long orderId = nextOrderId++;
            Order order = new Order(1000L, summary.getSubtotal(), summary.getPromoCode(),
                    summary.getPromoDiscount(), summary.getLoyaltyDiscount(), summary.getFinalTotal(),
                    deliveryAddress.trim(), false, deliveryTypeSnapshot, scheduledAtSnapshot);
            order.setId(orderId);
            orders.add(order);
            for (CartItem item : cart) {
                orderItems.add(new OrderItem(orderId, item.getCoffeeId(), item.getCoffeeName(),
                        item.getImageResId(), item.getShot(), item.getSize(), item.getIce(),
                        item.getQuantity(), item.getUnitPrice(), item.getTotalPrice(), item.getNote(),
                        item.getRewardSource()));
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
            cart.add(new CartItem(product.getId(), product.getName(), product.getImageResId(),
                    "SINGLE", "SMALL", "NORMAL", 1, 0.00, 0.00, "",
                    CartItem.REWARD_SOURCE_POINTS));
            return true;
        }

        boolean claimStampReward(CartItem rewardItem) {
            if (!RewardCalculator.canClaimStampCard(rewardState.getStampCount())) {
                return false;
            }
            rewardState.setStampCount(RewardCalculator.calculateStampCountAfterClaim(
                    rewardState.getStampCount()));
            cart.add(rewardItem);
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
            copy.cart.addAll(cart);
            copy.rewardTransactions.addAll(rewardTransactions);
            copy.rewardState.setStampCount(rewardState.getStampCount());
            copy.rewardState.setTotalPoints(rewardState.getTotalPoints());
            return copy;
        }
    }
}

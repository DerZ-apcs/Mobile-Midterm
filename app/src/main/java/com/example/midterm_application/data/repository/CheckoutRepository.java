package com.example.midterm_application.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.local.AppDatabase;
import com.example.midterm_application.data.local.CartDao;
import com.example.midterm_application.data.local.OrderDao;
import com.example.midterm_application.data.local.RewardDao;
import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.CheckoutSummary;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderItem;
import com.example.midterm_application.data.model.PromoCode;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.UserProfile;
import com.example.midterm_application.utils.CheckoutPriceCalculator;
import com.example.midterm_application.utils.RewardCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckoutRepository {
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private final AppDatabase database;
    private final CartDao cartDao;
    private final OrderDao orderDao;
    private final RewardDao rewardDao;
    private final ProfileRepository profileRepository;
    private final PromoRepository promoRepository;
    private final LiveData<List<CartItem>> cartItems;
    private final LiveData<RewardState> rewardState;

    public CheckoutRepository(Application application) {
        database = AppDatabase.getInstance(application);
        cartDao = database.cartDao();
        orderDao = database.orderDao();
        rewardDao = database.rewardDao();
        profileRepository = new ProfileRepository(application);
        promoRepository = new PromoRepository();
        cartItems = cartDao.getAllCartItems();
        rewardState = rewardDao.getRewardState();
        databaseExecutor.execute(() -> rewardDao.insertRewardState(RewardState.initial()));
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<RewardState> getRewardState() {
        return rewardState;
    }

    public UserProfile getProfile() {
        return profileRepository.getProfile();
    }

    public String normalizePromoCode(String promoCode) {
        return promoRepository.normalizeCode(promoCode);
    }

    public CheckoutSummary calculateSummary(List<CartItem> items, RewardState state,
                                            String rawPromoCode, boolean loyaltyRequested) {
        String normalizedCode = promoRepository.normalizeCode(rawPromoCode);
        PromoCode promoCode = promoRepository.findPromoCode(normalizedCode);
        boolean loyaltyAvailable = state != null
                && RewardCalculator.canClaimStampCard(state.getStampCount());
        return CheckoutPriceCalculator.calculate(items, promoCode, normalizedCode,
                loyaltyRequested, loyaltyAvailable);
    }

    public void placeOrder(String deliveryAddress, String rawPromoCode, boolean loyaltyRequested,
                           PlaceOrderCallback callback) {
        databaseExecutor.execute(() -> {
            PlaceOrderResult result;
            try {
                long orderId = database.runInTransaction(new Callable<Long>() {
                    @Override
                    public Long call() {
                        List<CartItem> currentCartItems = cartDao.getAllCartItemsSync();
                        if (currentCartItems == null || currentCartItems.isEmpty()) {
                            throw new CheckoutException("Cart is empty");
                        }

                        String addressSnapshot = deliveryAddress == null ? "" : deliveryAddress.trim();
                        if (addressSnapshot.isEmpty()) {
                            throw new CheckoutException("Delivery address is required");
                        }

                        rewardDao.insertRewardState(RewardState.initial());
                        RewardState currentRewardState = rewardDao.getRewardStateSync();
                        boolean loyaltyAvailable = currentRewardState != null
                                && RewardCalculator.canClaimStampCard(currentRewardState.getStampCount());
                        if (loyaltyRequested && !loyaltyAvailable) {
                            throw new CheckoutException("Free drink reward is no longer available");
                        }

                        String normalizedPromoCode = promoRepository.normalizeCode(rawPromoCode);
                        PromoCode promoCode = promoRepository.findPromoCode(normalizedPromoCode);
                        CheckoutSummary summary = CheckoutPriceCalculator.calculate(
                                currentCartItems,
                                promoCode,
                                normalizedPromoCode,
                                loyaltyRequested,
                                loyaltyAvailable);
                        if (!normalizedPromoCode.isEmpty() && !summary.isPromoAccepted()) {
                            throw new CheckoutException(summary.getPromoMessage());
                        }

                        Order order = new Order(
                                System.currentTimeMillis(),
                                summary.getSubtotal(),
                                summary.isPromoAccepted() ? summary.getPromoCode() : "",
                                summary.getPromoDiscount(),
                                summary.getLoyaltyDiscount(),
                                summary.getFinalTotal(),
                                addressSnapshot,
                                loyaltyRequested);
                        long insertedOrderId = orderDao.insertOrder(order);
                        List<OrderItem> orderItems = new ArrayList<>();
                        for (CartItem item : currentCartItems) {
                            orderItems.add(new OrderItem(
                                    insertedOrderId,
                                    item.getCoffeeId(),
                                    item.getCoffeeName(),
                                    item.getImageResId(),
                                    item.getShot(),
                                    item.getSize(),
                                    item.getIce(),
                                    item.getQuantity(),
                                    item.getUnitPrice(),
                                    item.getTotalPrice(),
                                    item.getNote()));
                        }
                        orderDao.insertOrderItems(orderItems);

                        if (loyaltyRequested) {
                            int updatedRows = rewardDao.claimFullStampCard();
                            if (updatedRows != 1) {
                                throw new CheckoutException("Free drink reward could not be consumed");
                            }
                        }

                        cartDao.clearCart();
                        return insertedOrderId;
                    }
                });
                result = PlaceOrderResult.success(orderId);
            } catch (CheckoutException exception) {
                result = PlaceOrderResult.failure(exception.getMessage());
            } catch (Exception exception) {
                result = PlaceOrderResult.failure("Place order failed");
            }

            if (callback != null) {
                callback.onPlaceOrderComplete(result);
            }
        });
    }

    public interface PlaceOrderCallback {
        void onPlaceOrderComplete(PlaceOrderResult result);
    }

    public static class PlaceOrderResult {
        private final boolean successful;
        private final long orderId;
        private final String errorMessage;

        private PlaceOrderResult(boolean successful, long orderId, String errorMessage) {
            this.successful = successful;
            this.orderId = orderId;
            this.errorMessage = errorMessage;
        }

        public static PlaceOrderResult success(long orderId) {
            return new PlaceOrderResult(true, orderId, null);
        }

        public static PlaceOrderResult failure(String errorMessage) {
            return new PlaceOrderResult(false, 0, errorMessage);
        }

        public boolean isSuccessful() {
            return successful;
        }

        public long getOrderId() {
            return orderId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private static class CheckoutException extends RuntimeException {
        CheckoutException(String message) {
            super(message);
        }
    }
}

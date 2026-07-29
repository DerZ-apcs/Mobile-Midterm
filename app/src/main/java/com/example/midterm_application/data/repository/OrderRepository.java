package com.example.midterm_application.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.local.AppDatabase;
import com.example.midterm_application.data.local.CartDao;
import com.example.midterm_application.data.local.OrderDao;
import com.example.midterm_application.data.local.RewardDao;
import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.Coffee;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderItem;
import com.example.midterm_application.data.model.OrderListItem;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;
import com.example.midterm_application.utils.PriceCalculator;
import com.example.midterm_application.utils.PriceCalculator.Ice;
import com.example.midterm_application.utils.PriceCalculator.Shot;
import com.example.midterm_application.utils.PriceCalculator.Size;
import com.example.midterm_application.utils.RewardCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderRepository {
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private final AppDatabase database;
    private final CartDao cartDao;
    private final OrderDao orderDao;
    private final RewardDao rewardDao;
    private final LiveData<List<OrderListItem>> ongoingOrders;
    private final LiveData<List<OrderListItem>> completedOrders;

    public OrderRepository(Application application) {
        database = AppDatabase.getInstance(application);
        cartDao = database.cartDao();
        orderDao = database.orderDao();
        rewardDao = database.rewardDao();
        ongoingOrders = orderDao.getOngoingOrders();
        completedOrders = orderDao.getCompletedOrders();
    }

    public LiveData<List<OrderListItem>> getOngoingOrders() {
        return ongoingOrders;
    }

    public LiveData<List<OrderListItem>> getCompletedOrders() {
        return completedOrders;
    }

    public void checkout(CheckoutCallback callback) {
        databaseExecutor.execute(() -> {
            try {
                long orderId = database.runInTransaction(new Callable<Long>() {
                    @Override
                    public Long call() {
                        List<CartItem> cartItems = cartDao.getAllCartItemsSync();
                        if (cartItems == null || cartItems.isEmpty()) {
                            throw new EmptyCartException();
                        }

                        double totalPrice = 0.00;
                        for (CartItem item : cartItems) {
                            totalPrice += item.getTotalPrice();
                        }

                        Order order = new Order(System.currentTimeMillis(), totalPrice);
                        long insertedOrderId = orderDao.insertOrder(order);
                        List<OrderItem> orderItems = new ArrayList<>();
                        for (CartItem item : cartItems) {
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
                                      item.getNote(),
                                      item.getRewardSource()));
                        }

                        orderDao.insertOrderItems(orderItems);
                        cartDao.clearCart();
                        return insertedOrderId;
                    }
                });
                callback.onCheckoutComplete(orderId, null);
            } catch (EmptyCartException exception) {
                callback.onCheckoutComplete(0, "Cart is empty");
            } catch (Exception exception) {
                callback.onCheckoutComplete(0, "Checkout failed");
            }
        });
    }

    public interface CheckoutCallback {
        void onCheckoutComplete(long orderId, String errorMessage);
    }

    public void reorderCompletedOrder(long orderId, ReorderCallback callback) {
        databaseExecutor.execute(() -> {
            try {
                int addedItems = database.runInTransaction(new Callable<Integer>() {
                    @Override
                    public Integer call() {
                        Order order = orderDao.getOrderByIdSync(orderId);
                        if (order == null || !Order.STATUS_COMPLETED.equals(order.getStatus())) {
                            throw new ReorderException();
                        }

                        List<OrderItem> orderItems = orderDao.getOrderItemsByOrderIdSync(orderId);
                        if (orderItems == null || orderItems.isEmpty()) {
                            throw new ReorderException();
                        }

                        List<CartItem> cartItems = new ArrayList<>();
                        for (OrderItem orderItem : orderItems) {
                            cartItems.add(createCartItemFromOrderItem(orderItem));
                        }
                        cartDao.insertAll(cartItems);
                        return cartItems.size();
                    }
                });
                callback.onReorderComplete(addedItems, null);
            } catch (Exception exception) {
                callback.onReorderComplete(0, "Could not reorder this order");
            }
        });
    }

    private CartItem createCartItemFromOrderItem(OrderItem orderItem) {
        int quantity = PriceCalculator.normalizeQuantity(orderItem.getQuantity());
        Coffee coffee = CoffeeRepository.getCoffeeById(orderItem.getCoffeeId());
        double unitPrice = orderItem.getUnitPrice();
        double totalPrice = orderItem.getTotalPrice();
        if (coffee != null) {
            Shot shot = parseShot(orderItem.getShot());
            Size size = parseSize(orderItem.getSize());
            Ice ice = parseIce(orderItem.getIce());
            unitPrice = PriceCalculator.calculateTotal(coffee.getBasePrice(), shot, size, ice, 1);
            totalPrice = PriceCalculator.calculateTotal(coffee.getBasePrice(), shot, size, ice, quantity);
        }
        return new CartItem(
                orderItem.getCoffeeId(),
                orderItem.getCoffeeName(),
                orderItem.getImageResId(),
                orderItem.getShot(),
                orderItem.getSize(),
                orderItem.getIce(),
                quantity,
                unitPrice,
                totalPrice,
                orderItem.getNote(),
                CartItem.REWARD_SOURCE_NONE);
    }

    private Shot parseShot(String value) {
        try {
            return Shot.valueOf(value);
        } catch (Exception exception) {
            return Shot.SINGLE;
        }
    }

    private Size parseSize(String value) {
        try {
            return Size.valueOf(value);
        } catch (Exception exception) {
            return Size.SMALL;
        }
    }

    private Ice parseIce(String value) {
        try {
            return Ice.valueOf(value);
        } catch (Exception exception) {
            return Ice.NORMAL;
        }
    }

    public interface ReorderCallback {
        void onReorderComplete(int addedItems, String errorMessage);
    }

    public void markOrderCompleted(long orderId) {
        databaseExecutor.execute(() -> database.runInTransaction(() -> {
            rewardDao.insertRewardState(RewardState.initial());
            Order order = orderDao.getOrderByIdSync(orderId);
            if (order == null || !Order.STATUS_ONGOING.equals(order.getStatus())) {
                return;
            }

            int updatedRows = orderDao.markOrderCompleted(orderId);
            if (updatedRows != 1) {
                return;
            }

            int earnedPoints = RewardCalculator.calculateEarnedPoints(order.getTotalPrice());
            RewardTransaction transaction = new RewardTransaction(
                    orderId,
                    System.currentTimeMillis(),
                    RewardTransaction.TYPE_EARN,
                    earnedPoints,
                    "Order #" + orderId + " completed");
            long insertedId = rewardDao.insertRewardTransaction(transaction);
            if (RewardCalculator.shouldApplyEarnReward(true, insertedId != -1L)) {
                rewardDao.addEarnedReward(earnedPoints);
            }
        }));
    }

    private static class EmptyCartException extends RuntimeException {
    }

    private static class ReorderException extends RuntimeException {
    }
}

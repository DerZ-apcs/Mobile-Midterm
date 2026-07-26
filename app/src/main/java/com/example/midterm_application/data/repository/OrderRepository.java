package com.example.midterm_application.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.local.AppDatabase;
import com.example.midterm_application.data.local.CartDao;
import com.example.midterm_application.data.local.OrderDao;
import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderItem;
import com.example.midterm_application.data.model.OrderListItem;

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
    private final LiveData<List<OrderListItem>> ongoingOrders;
    private final LiveData<List<OrderListItem>> completedOrders;

    public OrderRepository(Application application) {
        database = AppDatabase.getInstance(application);
        cartDao = database.cartDao();
        orderDao = database.orderDao();
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
                                    item.getTotalPrice()));
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

    public void markOrderCompleted(long orderId) {
        databaseExecutor.execute(() -> orderDao.markOrderCompleted(orderId));
    }

    private static class EmptyCartException extends RuntimeException {
    }
}

package com.example.midterm_application.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderItem;
import com.example.midterm_application.data.model.OrderListItem;

import java.util.List;

@Dao
public interface OrderDao {
    @Query("SELECT orders.id AS id, orders.createdAt AS createdAt, "
            + "orders.totalPrice AS totalPrice, orders.status AS status, "
            + "IFNULL(GROUP_CONCAT(order_items.coffeeName || ' x' || order_items.quantity, ', '), '') AS itemSummary "
            + "FROM orders LEFT JOIN order_items ON orders.id = order_items.orderId "
            + "WHERE orders.status = 'ONGOING' "
            + "GROUP BY orders.id, orders.createdAt, orders.totalPrice, orders.status "
            + "ORDER BY orders.createdAt DESC")
    LiveData<List<OrderListItem>> getOngoingOrders();

    @Query("SELECT orders.id AS id, orders.createdAt AS createdAt, "
            + "orders.totalPrice AS totalPrice, orders.status AS status, "
            + "IFNULL(GROUP_CONCAT(order_items.coffeeName || ' x' || order_items.quantity, ', '), '') AS itemSummary "
            + "FROM orders LEFT JOIN order_items ON orders.id = order_items.orderId "
            + "WHERE orders.status = 'COMPLETED' "
            + "GROUP BY orders.id, orders.createdAt, orders.totalPrice, orders.status "
            + "ORDER BY orders.createdAt DESC")
    LiveData<List<OrderListItem>> getCompletedOrders();

    @Insert
    long insertOrder(Order order);

    @Insert
    void insertOrderItems(List<OrderItem> orderItems);

    @Query("UPDATE orders SET status = 'COMPLETED' WHERE id = :orderId AND status = 'ONGOING'")
    int markOrderCompleted(long orderId);
}

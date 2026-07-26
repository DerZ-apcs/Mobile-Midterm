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
            + "IFNULL(GROUP_CONCAT(order_items.coffeeName || ' x' || order_items.quantity "
            + "|| CASE WHEN order_items.note IS NOT NULL AND TRIM(order_items.note) != '' "
            + "THEN ' (' || order_items.note || ')' ELSE '' END, ', '), '') AS itemSummary "
            + "FROM orders LEFT JOIN order_items ON orders.id = order_items.orderId "
            + "WHERE orders.status = 'ONGOING' "
            + "GROUP BY orders.id, orders.createdAt, orders.totalPrice, orders.status "
            + "ORDER BY orders.createdAt DESC")
    LiveData<List<OrderListItem>> getOngoingOrders();

    @Query("SELECT orders.id AS id, orders.createdAt AS createdAt, "
            + "orders.totalPrice AS totalPrice, orders.status AS status, "
            + "IFNULL(GROUP_CONCAT(order_items.coffeeName || ' x' || order_items.quantity "
            + "|| CASE WHEN order_items.note IS NOT NULL AND TRIM(order_items.note) != '' "
            + "THEN ' (' || order_items.note || ')' ELSE '' END, ', '), '') AS itemSummary "
            + "FROM orders LEFT JOIN order_items ON orders.id = order_items.orderId "
            + "WHERE orders.status = 'COMPLETED' "
            + "GROUP BY orders.id, orders.createdAt, orders.totalPrice, orders.status "
            + "ORDER BY orders.createdAt DESC")
    LiveData<List<OrderListItem>> getCompletedOrders();

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    Order getOrderByIdSync(long orderId);

    @Query("SELECT * FROM order_items WHERE orderId = :orderId ORDER BY id ASC")
    List<OrderItem> getOrderItemsByOrderIdSync(long orderId);

    @Insert
    long insertOrder(Order order);

    @Insert
    void insertOrderItems(List<OrderItem> orderItems);

    @Query("UPDATE orders SET status = 'COMPLETED' WHERE id = :orderId AND status = 'ONGOING'")
    int markOrderCompleted(long orderId);
}

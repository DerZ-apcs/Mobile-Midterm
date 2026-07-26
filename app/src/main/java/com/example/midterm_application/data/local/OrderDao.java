package com.example.midterm_application.data.local;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.midterm_application.data.model.Order;
import com.example.midterm_application.data.model.OrderItem;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    long insertOrder(Order order);

    @Insert
    void insertOrderItems(List<OrderItem> orderItems);
}

package com.example.midterm_application.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.midterm_application.data.model.OrderReview;

@Dao
public interface OrderReviewDao {
    @Query("SELECT * FROM order_reviews WHERE orderId = :orderId LIMIT 1")
    LiveData<OrderReview> getReviewForOrder(long orderId);

    @Query("SELECT * FROM order_reviews WHERE orderId = :orderId LIMIT 1")
    OrderReview getReviewForOrderSync(long orderId);

    @Insert
    long insert(OrderReview review);

    @Update
    int update(OrderReview review);
}

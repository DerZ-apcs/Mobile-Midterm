package com.example.midterm_application.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_reviews",
        foreignKeys = @ForeignKey(entity = Order.class,
                parentColumns = "id",
                childColumns = "orderId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"orderId"}, unique = true)})
public class OrderReview {
    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long orderId;
    private int rating;
    private String comment;
    private long createdAt;
    private long updatedAt;

    public OrderReview() {
    }

    @Ignore
    public OrderReview(long orderId, int rating, String comment, long createdAt, long updatedAt) {
        this.orderId = orderId;
        this.rating = rating;
        setComment(comment);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static boolean isValidRating(int rating) {
        return rating >= MIN_RATING && rating <= MAX_RATING;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? "" : comment;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

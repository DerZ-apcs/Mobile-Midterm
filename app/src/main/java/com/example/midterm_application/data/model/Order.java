package com.example.midterm_application.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "orders")
public class Order {
    public static final String STATUS_ONGOING = "ONGOING";
    public static final String STATUS_COMPLETED = "COMPLETED";

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long createdAt;
    private double totalPrice;
    private String status = STATUS_ONGOING;

    public Order() {
    }

    @Ignore
    public Order(long createdAt, double totalPrice) {
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
        this.status = STATUS_ONGOING;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? STATUS_ONGOING : status;
    }
}

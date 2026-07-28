package com.example.midterm_application.data.model;

public class OrderListItem {
    public long id;
    public long createdAt;
    public double totalPrice;
    public String status;
    public String itemSummary;
    public String deliveryType;
    public long scheduledAt;

    public long getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getItemSummary() {
        return itemSummary;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public long getScheduledAt() {
        return scheduledAt;
    }
}

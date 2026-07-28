package com.example.midterm_application.data.model;

public class OrderListItem {
    public long id;
    public long createdAt;
    public double totalPrice;
    public String status;
    public String itemSummary;
    public String deliveryType;
    public long scheduledAt;
    public int reviewRating;
    public String reviewComment;
    public long reviewUpdatedAt;

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

    public int getReviewRating() {
        return reviewRating;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public long getReviewUpdatedAt() {
        return reviewUpdatedAt;
    }

    public boolean hasReview() {
        return OrderReview.isValidRating(reviewRating);
    }
}

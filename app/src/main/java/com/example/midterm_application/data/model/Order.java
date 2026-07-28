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
    private double subtotal;
    private String promoCode;
    private double promoDiscount;
    private double loyaltyDiscount;
    private double finalTotal;
    private String deliveryAddress;
    private boolean loyaltyRewardUsed;

    public Order() {
    }

    @Ignore
    public Order(long createdAt, double totalPrice) {
        this(createdAt, totalPrice, "", 0.00, 0.00, totalPrice, "", false);
    }

    @Ignore
    public Order(long createdAt, double subtotal, String promoCode, double promoDiscount,
                 double loyaltyDiscount, double finalTotal, String deliveryAddress,
                 boolean loyaltyRewardUsed) {
        this.createdAt = createdAt;
        this.totalPrice = finalTotal;
        this.status = STATUS_ONGOING;
        this.subtotal = subtotal;
        this.promoCode = promoCode;
        this.promoDiscount = promoDiscount;
        this.loyaltyDiscount = loyaltyDiscount;
        this.finalTotal = finalTotal;
        this.deliveryAddress = deliveryAddress;
        this.loyaltyRewardUsed = loyaltyRewardUsed;
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

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public double getPromoDiscount() {
        return promoDiscount;
    }

    public void setPromoDiscount(double promoDiscount) {
        this.promoDiscount = promoDiscount;
    }

    public double getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public void setLoyaltyDiscount(double loyaltyDiscount) {
        this.loyaltyDiscount = loyaltyDiscount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public boolean isLoyaltyRewardUsed() {
        return loyaltyRewardUsed;
    }

    public void setLoyaltyRewardUsed(boolean loyaltyRewardUsed) {
        this.loyaltyRewardUsed = loyaltyRewardUsed;
    }
}

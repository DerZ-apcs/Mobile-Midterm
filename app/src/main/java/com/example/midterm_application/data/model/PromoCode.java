package com.example.midterm_application.data.model;

public class PromoCode {
    public enum DiscountType {
        PERCENT,
        FIXED
    }

    private final String code;
    private final DiscountType discountType;
    private final double value;
    private final double minimumSubtotal;

    public PromoCode(String code, DiscountType discountType, double value, double minimumSubtotal) {
        this.code = code;
        this.discountType = discountType;
        this.value = value;
        this.minimumSubtotal = minimumSubtotal;
    }

    public String getCode() {
        return code;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public double getValue() {
        return value;
    }

    public double getMinimumSubtotal() {
        return minimumSubtotal;
    }
}

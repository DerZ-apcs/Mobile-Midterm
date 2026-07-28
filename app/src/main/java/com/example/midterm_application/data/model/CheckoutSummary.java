package com.example.midterm_application.data.model;

public class CheckoutSummary {
    private final double subtotal;
    private final double loyaltyDiscount;
    private final double promoDiscount;
    private final double finalTotal;
    private final String promoCode;
    private final boolean promoAccepted;
    private final String promoMessage;

    public CheckoutSummary(double subtotal, double loyaltyDiscount, double promoDiscount,
                           double finalTotal, String promoCode, boolean promoAccepted,
                           String promoMessage) {
        this.subtotal = subtotal;
        this.loyaltyDiscount = loyaltyDiscount;
        this.promoDiscount = promoDiscount;
        this.finalTotal = finalTotal;
        this.promoCode = promoCode;
        this.promoAccepted = promoAccepted;
        this.promoMessage = promoMessage;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public double getPromoDiscount() {
        return promoDiscount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public boolean isPromoAccepted() {
        return promoAccepted;
    }

    public String getPromoMessage() {
        return promoMessage;
    }
}

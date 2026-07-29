package com.example.midterm_application.utils;

import com.example.midterm_application.data.model.CartItem;
import com.example.midterm_application.data.model.CheckoutSummary;
import com.example.midterm_application.data.model.PromoCode;

import java.util.List;

public final class CheckoutPriceCalculator {
    private CheckoutPriceCalculator() {
    }

    public static CheckoutSummary calculate(List<CartItem> cartItems, PromoCode promoCode,
                                             String normalizedPromoCode, boolean loyaltyRequested,
                                             boolean loyaltyAvailable) {
        double subtotal = calculateSubtotal(cartItems);
        double loyaltyDiscount = 0.00;
        double eligibleSubtotal = Math.max(0.00, subtotal - loyaltyDiscount);
        PromoResult promoResult = calculatePromoDiscount(promoCode, normalizedPromoCode, eligibleSubtotal);
        double finalTotal = Math.max(0.00, subtotal - loyaltyDiscount - promoResult.discount);
        return new CheckoutSummary(
                roundCurrency(subtotal),
                roundCurrency(loyaltyDiscount),
                roundCurrency(promoResult.discount),
                roundCurrency(finalTotal),
                promoResult.normalizedCode,
                promoResult.accepted,
                promoResult.message);
    }

    private static double calculateSubtotal(List<CartItem> cartItems) {
        double subtotal = 0.00;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item != null) {
                    subtotal += Math.max(0.00, item.getTotalPrice());
                }
            }
        }
        return subtotal;
    }

    private static PromoResult calculatePromoDiscount(PromoCode promoCode, String normalizedPromoCode,
                                                      double eligibleSubtotal) {
        String safeCode = normalizedPromoCode == null ? "" : normalizedPromoCode;
        if (safeCode.isEmpty()) {
            return PromoResult.empty();
        }
        if (promoCode == null) {
            return PromoResult.rejected(safeCode, "Invalid promo code");
        }
        if (eligibleSubtotal < promoCode.getMinimumSubtotal()) {
            return PromoResult.rejected(safeCode,
                    String.format(java.util.Locale.US, "%s requires at least $%.2f",
                            promoCode.getCode(), promoCode.getMinimumSubtotal()));
        }

        double discount;
        if (promoCode.getDiscountType() == PromoCode.DiscountType.PERCENT) {
            discount = eligibleSubtotal * (promoCode.getValue() / 100.00);
        } else {
            discount = promoCode.getValue();
        }
        discount = Math.min(Math.max(0.00, discount), eligibleSubtotal);
        return PromoResult.accepted(promoCode.getCode(), roundCurrency(discount), "Promo applied");
    }

    public static double roundCurrency(double value) {
        return Math.round(value * 100.00) / 100.00;
    }

    private static class PromoResult {
        private final String normalizedCode;
        private final double discount;
        private final boolean accepted;
        private final String message;

        private PromoResult(String normalizedCode, double discount, boolean accepted, String message) {
            this.normalizedCode = normalizedCode;
            this.discount = discount;
            this.accepted = accepted;
            this.message = message;
        }

        private static PromoResult empty() {
            return new PromoResult("", 0.00, false, "");
        }

        private static PromoResult accepted(String normalizedCode, double discount, String message) {
            return new PromoResult(normalizedCode, discount, true, message);
        }

        private static PromoResult rejected(String normalizedCode, String message) {
            return new PromoResult(normalizedCode, 0.00, false, message);
        }
    }
}

package com.example.midterm_application.data.repository;

import com.example.midterm_application.data.model.PromoCode;

import java.util.Locale;

public class PromoRepository {
    public PromoCode findPromoCode(String rawCode) {
        String normalizedCode = normalizeCode(rawCode);
        if (normalizedCode.isEmpty()) {
            return null;
        }
        if ("CODECUP10".equals(normalizedCode)) {
            return new PromoCode(normalizedCode, PromoCode.DiscountType.PERCENT, 10.00, 0.00);
        }
        if ("WELCOME5".equals(normalizedCode)) {
            return new PromoCode(normalizedCode, PromoCode.DiscountType.FIXED, 5.00, 15.00);
        }
        return null;
    }

    public String normalizeCode(String rawCode) {
        return rawCode == null ? "" : rawCode.trim().toUpperCase(Locale.US);
    }
}

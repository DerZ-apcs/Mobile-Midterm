package com.example.midterm_application.utils;

public final class PriceCalculator {
    public static final int MIN_QUANTITY = 1;
    public static final int MAX_QUANTITY = 20;

    private static final double DOUBLE_SHOT_MODIFIER = 0.50;
    private static final double MEDIUM_SIZE_MODIFIER = 0.50;
    private static final double LARGE_SIZE_MODIFIER = 1.00;

    private PriceCalculator() {
    }

    public static double calculateTotal(double basePrice, Shot shot, Size size, Ice ice, int quantity) {
        int normalizedQuantity = normalizeQuantity(quantity);
        return (basePrice + getShotModifier(shot) + getSizeModifier(size) + getIceModifier(ice))
                * normalizedQuantity;
    }

    public static int normalizeQuantity(int quantity) {
        if (quantity < MIN_QUANTITY) {
            return MIN_QUANTITY;
        }
        if (quantity > MAX_QUANTITY) {
            return MAX_QUANTITY;
        }
        return quantity;
    }

    public static double getShotModifier(Shot shot) {
        return shot == Shot.DOUBLE ? DOUBLE_SHOT_MODIFIER : 0.00;
    }

    public static double getSizeModifier(Size size) {
        if (size == Size.MEDIUM) {
            return MEDIUM_SIZE_MODIFIER;
        }
        if (size == Size.LARGE) {
            return LARGE_SIZE_MODIFIER;
        }
        return 0.00;
    }

    public static double getIceModifier(Ice ice) {
        return 0.00;
    }

    public enum Shot {
        SINGLE,
        DOUBLE
    }

    public enum Size {
        SMALL,
        MEDIUM,
        LARGE
    }

    public enum Ice {
        NO_ICE,
        LESS_ICE,
        NORMAL
    }
}

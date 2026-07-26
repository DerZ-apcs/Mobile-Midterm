package com.example.midterm_application.utils;

public final class RewardCalculator {
    public static final int MAX_STAMPS = 8;

    private RewardCalculator() {
    }

    public static int calculateEarnedPoints(double orderTotal) {
        return (int) Math.round(orderTotal * 10.0);
    }

    public static int capStampCount(int stampCount) {
        if (stampCount < 0) {
            return 0;
        }
        if (stampCount > MAX_STAMPS) {
            return MAX_STAMPS;
        }
        return stampCount;
    }
}

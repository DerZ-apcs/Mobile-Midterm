package com.example.midterm_application.utils;

public final class RewardCalculator {
    public static final int MAX_STAMPS = 8;

    private RewardCalculator() {
    }

    public static int calculateEarnedPoints(double orderTotal) {
        return (int) Math.round(orderTotal * 10.0);
    }

    public static int calculateTotalPointsAfterEarn(int currentTotalPoints, int earnedPoints) {
        return currentTotalPoints + earnedPoints;
    }

    public static int calculateStampCountAfterEarn(int currentStampCount) {
        return capStampCount(currentStampCount + 1);
    }

    public static boolean canClaimStampCard(int stampCount) {
        return stampCount == MAX_STAMPS;
    }

    public static int calculateStampCountAfterClaim(int stampCount) {
        return canClaimStampCard(stampCount) ? 0 : capStampCount(stampCount);
    }

    public static boolean shouldApplyEarnReward(boolean orderTransitioned, boolean earnTransactionInserted) {
        return orderTransitioned && earnTransactionInserted;
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

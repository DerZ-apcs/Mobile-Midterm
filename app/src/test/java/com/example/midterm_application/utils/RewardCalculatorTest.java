package com.example.midterm_application.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RewardCalculatorTest {
    @Test
    public void calculatesEarnedPointsFromOrderTotal() {
        assertEquals(0, RewardCalculator.calculateEarnedPoints(0.00));
        assertEquals(30, RewardCalculator.calculateEarnedPoints(3.00));
        assertEquals(45, RewardCalculator.calculateEarnedPoints(4.50));
        assertEquals(125, RewardCalculator.calculateEarnedPoints(12.49));
        assertEquals(125, RewardCalculator.calculateEarnedPoints(12.50));
        assertEquals(126, RewardCalculator.calculateEarnedPoints(12.55));
    }

    @Test
    public void accumulatesPointsForDifferentCompletedOrders() {
        int totalPoints = 0;

        totalPoints = RewardCalculator.calculateTotalPointsAfterEarn(
                totalPoints,
                RewardCalculator.calculateEarnedPoints(3.00));
        totalPoints = RewardCalculator.calculateTotalPointsAfterEarn(
                totalPoints,
                RewardCalculator.calculateEarnedPoints(4.50));

        assertEquals(75, totalPoints);
    }

    @Test
    public void duplicateCompletionDoesNotAddSecondRewardWhenAwardIsSkipped() {
        int totalPoints = 0;
        int stampCount = 0;

        if (RewardCalculator.shouldApplyEarnReward(true, true)) {
            totalPoints = RewardCalculator.calculateTotalPointsAfterEarn(totalPoints, 30);
            stampCount = RewardCalculator.calculateStampCountAfterEarn(stampCount);
        }
        if (RewardCalculator.shouldApplyEarnReward(false, false)) {
            totalPoints = RewardCalculator.calculateTotalPointsAfterEarn(totalPoints, 30);
            stampCount = RewardCalculator.calculateStampCountAfterEarn(stampCount);
        }

        assertEquals(30, totalPoints);
        assertEquals(1, stampCount);
    }

    @Test
    public void duplicateEarnTransactionDoesNotApplyReward() {
        assertEquals(false, RewardCalculator.shouldApplyEarnReward(true, false));
    }

    @Test
    public void incrementsStampSevenToEightAndCapsAtEight() {
        assertEquals(8, RewardCalculator.calculateStampCountAfterEarn(7));
        assertEquals(8, RewardCalculator.calculateStampCountAfterEarn(8));
    }

    @Test
    public void claimResetsOnlyFullStampCard() {
        assertEquals(0, RewardCalculator.calculateStampCountAfterClaim(8));
        assertEquals(7, RewardCalculator.calculateStampCountAfterClaim(7));
    }

    @Test
    public void capsStampCountWithinAllowedRange() {
        assertEquals(0, RewardCalculator.capStampCount(-1));
        assertEquals(0, RewardCalculator.capStampCount(0));
        assertEquals(4, RewardCalculator.capStampCount(4));
        assertEquals(8, RewardCalculator.capStampCount(8));
        assertEquals(8, RewardCalculator.capStampCount(9));
    }
}

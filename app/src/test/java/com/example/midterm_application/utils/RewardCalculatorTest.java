package com.example.midterm_application.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RewardCalculatorTest {
    @Test
    public void calculatesEarnedPointsFromOrderTotal() {
        assertEquals(0, RewardCalculator.calculateEarnedPoints(0.00));
        assertEquals(30, RewardCalculator.calculateEarnedPoints(3.00));
        assertEquals(125, RewardCalculator.calculateEarnedPoints(12.49));
        assertEquals(125, RewardCalculator.calculateEarnedPoints(12.50));
        assertEquals(126, RewardCalculator.calculateEarnedPoints(12.55));
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

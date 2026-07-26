package com.example.midterm_application.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.midterm_application.data.model.RewardTransaction;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void allowsRedeemOnlyWhenPointsCoverCost() {
        assertTrue(RewardCalculator.canRedeem(160, 160));
        assertTrue(RewardCalculator.canRedeem(200, 160));
        assertFalse(RewardCalculator.canRedeem(159, 160));
        assertFalse(RewardCalculator.canRedeem(160, 0));
    }

    @Test
    public void redeemDeductsCostWithoutGoingNegative() {
        assertEquals(40, RewardCalculator.calculateTotalPointsAfterRedeem(200, 160));
        assertEquals(159, RewardCalculator.calculateTotalPointsAfterRedeem(159, 160));
        assertEquals(100, RewardCalculator.calculateTotalPointsAfterRedeem(100, -1));
    }

    @Test
    public void redeemCost120From200Leaves80() {
        RewardLedger ledger = new RewardLedger(200, 3);

        assertTrue(ledger.redeem(120, "Americano"));

        assertEquals(80, ledger.totalPoints);
    }

    @Test
    public void redeemCost120From100IsRejectedAndLeavesBalanceUnchanged() {
        RewardLedger ledger = new RewardLedger(100, 3);

        assertFalse(ledger.redeem(120, "Americano"));

        assertEquals(100, ledger.totalPoints);
        assertEquals(0, ledger.countTransactions(RewardTransaction.TYPE_REDEEM));
    }

    @Test
    public void redeemCost120From120LeavesZero() {
        RewardLedger ledger = new RewardLedger(120, 3);

        assertTrue(ledger.redeem(120, "Americano"));

        assertEquals(0, ledger.totalPoints);
    }

    @Test
    public void repeatedRedeemCannotCreateNegativeBalance() {
        RewardLedger ledger = new RewardLedger(200, 3);

        assertTrue(ledger.redeem(120, "Americano"));
        assertFalse(ledger.redeem(120, "Americano"));

        assertEquals(80, ledger.totalPoints);
        assertEquals(1, ledger.countTransactions(RewardTransaction.TYPE_REDEEM));
    }

    @Test
    public void successfulRedeemCreatesExactlyOneRedeemTransaction() {
        RewardLedger ledger = new RewardLedger(200, 3);

        assertTrue(ledger.redeem(120, "Americano"));

        assertEquals(1, ledger.countTransactions(RewardTransaction.TYPE_REDEEM));
        RewardTransaction transaction = ledger.transactions.get(0);
        assertEquals(RewardTransaction.TYPE_REDEEM, transaction.getType());
        assertEquals(-120, transaction.getPoints());
        assertEquals("Americano", transaction.getDescription());
    }

    @Test
    public void failedRedeemCreatesZeroRedeemTransactions() {
        RewardLedger ledger = new RewardLedger(100, 3);

        assertFalse(ledger.redeem(120, "Americano"));

        assertEquals(0, ledger.countTransactions(RewardTransaction.TYPE_REDEEM));
    }

    @Test
    public void redeemDoesNotAlterStampCount() {
        RewardLedger ledger = new RewardLedger(200, 7);

        assertTrue(ledger.redeem(120, "Americano"));

        assertEquals(7, ledger.stampCount);
    }

    @Test
    public void earnThenRedeemSequenceProducesCorrectFinalPoints() {
        RewardLedger ledger = new RewardLedger(0, 0);

        ledger.earn(20.00, 15L);
        assertTrue(ledger.redeem(120, "Americano"));

        assertEquals(80, ledger.totalPoints);
        assertEquals(1, ledger.countTransactions(RewardTransaction.TYPE_EARN));
        assertEquals(1, ledger.countTransactions(RewardTransaction.TYPE_REDEEM));
    }

    @Test
    public void capsStampCountWithinAllowedRange() {
        assertEquals(0, RewardCalculator.capStampCount(-1));
        assertEquals(0, RewardCalculator.capStampCount(0));
        assertEquals(4, RewardCalculator.capStampCount(4));
        assertEquals(8, RewardCalculator.capStampCount(8));
        assertEquals(8, RewardCalculator.capStampCount(9));
    }

    private static class RewardLedger {
        int totalPoints;
        int stampCount;
        final List<RewardTransaction> transactions = new ArrayList<>();

        RewardLedger(int totalPoints, int stampCount) {
            this.totalPoints = totalPoints;
            this.stampCount = stampCount;
        }

        void earn(double orderTotal, long orderId) {
            int earnedPoints = RewardCalculator.calculateEarnedPoints(orderTotal);
            totalPoints = RewardCalculator.calculateTotalPointsAfterEarn(totalPoints, earnedPoints);
            stampCount = RewardCalculator.calculateStampCountAfterEarn(stampCount);
            transactions.add(new RewardTransaction(orderId, 1L, RewardTransaction.TYPE_EARN,
                    earnedPoints, "Order #" + orderId + " completed"));
        }

        boolean redeem(int pointCost, String name) {
            if (!RewardCalculator.canRedeem(totalPoints, pointCost)) {
                return false;
            }
            totalPoints = RewardCalculator.calculateTotalPointsAfterRedeem(totalPoints, pointCost);
            transactions.add(new RewardTransaction(null, 2L, RewardTransaction.TYPE_REDEEM,
                    -pointCost, name));
            return true;
        }

        int countTransactions(String type) {
            int count = 0;
            for (RewardTransaction transaction : transactions) {
                if (type.equals(transaction.getType())) {
                    count++;
                }
            }
            return count;
        }
    }
}

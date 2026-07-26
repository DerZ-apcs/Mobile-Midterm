package com.example.midterm_application.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.local.AppDatabase;
import com.example.midterm_application.data.local.RewardDao;
import com.example.midterm_application.data.model.RewardProduct;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;
import com.example.midterm_application.utils.RewardCalculator;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RewardRepository {
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private final AppDatabase database;
    private final RewardDao rewardDao;
    private final LiveData<RewardState> rewardState;
    private final LiveData<List<RewardTransaction>> rewardTransactions;

    public RewardRepository(Application application) {
        database = AppDatabase.getInstance(application);
        rewardDao = database.rewardDao();
        databaseExecutor.execute(() -> rewardDao.insertRewardState(RewardState.initial()));
        rewardState = rewardDao.getRewardState();
        rewardTransactions = rewardDao.getRewardTransactions();
    }

    public LiveData<RewardState> getRewardState() {
        return rewardState;
    }

    public LiveData<List<RewardTransaction>> getRewardTransactions() {
        return rewardTransactions;
    }

    public void claimFullStampCard() {
        databaseExecutor.execute(rewardDao::claimFullStampCard);
    }

    public void redeemReward(RewardProduct product, RedemptionCallback callback) {
        databaseExecutor.execute(() -> {
            RedemptionResult result;
            try {
                result = database.runInTransaction(new Callable<RedemptionResult>() {
                    @Override
                    public RedemptionResult call() {
                        if (product == null || product.getPointCost() <= 0) {
                            return RedemptionResult.failure("Reward cannot be redeemed");
                        }

                        rewardDao.insertRewardState(RewardState.initial());
                        RewardState state = rewardDao.getRewardStateSync();
                        int currentPoints = state == null ? 0 : state.getTotalPoints();
                        if (!RewardCalculator.canRedeem(currentPoints, product.getPointCost())) {
                            return RedemptionResult.failure("Not enough points for " + product.getName());
                        }

                        int deductedRows = rewardDao.deductPointsIfAvailable(product.getPointCost());
                        if (deductedRows != 1) {
                            return RedemptionResult.failure("Not enough points for " + product.getName());
                        }

                        RewardTransaction transaction = new RewardTransaction(
                                null,
                                System.currentTimeMillis(),
                                RewardTransaction.TYPE_REDEEM,
                                -product.getPointCost(),
                                product.getName());
                        long transactionId = rewardDao.insertRewardTransactionStrict(transaction);
                        if (transactionId <= 0) {
                            throw new IllegalStateException("Redeem transaction was not created");
                        }
                        return RedemptionResult.success(product.getName() + " redeemed");
                    }
                });
            } catch (Exception exception) {
                result = RedemptionResult.failure("Redemption failed");
            }

            if (callback != null) {
                callback.onRedemptionComplete(result);
            }
        });
    }

    public interface RedemptionCallback {
        void onRedemptionComplete(RedemptionResult result);
    }

    public static class RedemptionResult {
        private final boolean successful;
        private final String message;

        private RedemptionResult(boolean successful, String message) {
            this.successful = successful;
            this.message = message;
        }

        public static RedemptionResult success(String message) {
            return new RedemptionResult(true, message);
        }

        public static RedemptionResult failure(String message) {
            return new RedemptionResult(false, message);
        }

        public boolean isSuccessful() {
            return successful;
        }

        public String getMessage() {
            return message;
        }
    }
}

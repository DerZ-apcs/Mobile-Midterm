package com.example.midterm_application.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.local.AppDatabase;
import com.example.midterm_application.data.local.RewardDao;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RewardRepository {
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private final RewardDao rewardDao;
    private final LiveData<RewardState> rewardState;
    private final LiveData<List<RewardTransaction>> rewardTransactions;

    public RewardRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
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
}

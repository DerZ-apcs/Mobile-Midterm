package com.example.midterm_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;
import com.example.midterm_application.data.repository.RewardRepository;

import java.util.List;

public class RewardViewModel extends AndroidViewModel {
    private final RewardRepository repository;

    public RewardViewModel(@NonNull Application application) {
        super(application);
        repository = new RewardRepository(application);
    }

    public LiveData<RewardState> getRewardState() {
        return repository.getRewardState();
    }

    public LiveData<List<RewardTransaction>> getRewardTransactions() {
        return repository.getRewardTransactions();
    }

    public void claimFullStampCard() {
        repository.claimFullStampCard();
    }
}

package com.example.midterm_application.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm_application.data.model.RewardProduct;
import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;
import com.example.midterm_application.data.repository.RewardRepository;

import java.util.List;

public class RewardViewModel extends AndroidViewModel {
    private final RewardRepository repository;
    private final MutableLiveData<Boolean> redemptionInProgress = new MutableLiveData<>(false);
    private final MutableLiveData<RewardRepository.RedemptionResult> redemptionResult = new MutableLiveData<>();

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

    public LiveData<Boolean> getRedemptionInProgress() {
        return redemptionInProgress;
    }

    public LiveData<RewardRepository.RedemptionResult> getRedemptionResult() {
        return redemptionResult;
    }

    public void redeemReward(RewardProduct product) {
        if (Boolean.TRUE.equals(redemptionInProgress.getValue())) {
            return;
        }
        redemptionInProgress.setValue(true);
        repository.redeemReward(product, result -> {
            redemptionResult.postValue(result);
            redemptionInProgress.postValue(false);
        });
    }

    public void clearRedemptionResult() {
        redemptionResult.setValue(null);
    }
}

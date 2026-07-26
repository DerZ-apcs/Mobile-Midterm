package com.example.midterm_application.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.midterm_application.data.model.RewardState;
import com.example.midterm_application.data.model.RewardTransaction;

import java.util.List;

@Dao
public interface RewardDao {
    @Query("SELECT * FROM reward_state WHERE id = 1")
    LiveData<RewardState> getRewardState();

    @Query("SELECT * FROM reward_transactions ORDER BY createdAt DESC, id DESC")
    LiveData<List<RewardTransaction>> getRewardTransactions();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRewardState(RewardState rewardState);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertRewardTransaction(RewardTransaction transaction);

    @Query("UPDATE reward_state "
            + "SET totalPoints = totalPoints + :points, "
            + "stampCount = CASE WHEN stampCount < 8 THEN stampCount + 1 ELSE 8 END "
            + "WHERE id = 1")
    int addEarnedReward(int points);

    @Query("UPDATE reward_state SET stampCount = 0 WHERE id = 1 AND stampCount = 8")
    int claimFullStampCard();
}

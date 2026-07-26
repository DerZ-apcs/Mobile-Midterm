package com.example.midterm_application.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "reward_state")
public class RewardState {
    public static final int SINGLE_ROW_ID = 1;

    @PrimaryKey
    private int id;
    private int stampCount;
    private int totalPoints;

    public RewardState() {
    }

    @Ignore
    public RewardState(int id, int stampCount, int totalPoints) {
        this.id = id;
        this.stampCount = stampCount;
        this.totalPoints = totalPoints;
    }

    public static RewardState initial() {
        return new RewardState(SINGLE_ROW_ID, 0, 0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStampCount() {
        return stampCount;
    }

    public void setStampCount(int stampCount) {
        this.stampCount = stampCount;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }
}

package com.example.midterm_application.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "reward_transactions",
        foreignKeys = @ForeignKey(entity = Order.class,
                parentColumns = "id",
                childColumns = "orderId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = {"orderId", "type"}, unique = true)})
public class RewardTransaction {
    public static final String TYPE_EARN = "EARN";
    public static final String TYPE_REDEEM = "REDEEM";

    @PrimaryKey(autoGenerate = true)
    private long id;
    private Long orderId;
    private long createdAt;
    private String type;
    private int points;
    private String description;

    public RewardTransaction() {
    }

    @Ignore
    public RewardTransaction(Long orderId, long createdAt, String type, int points, String description) {
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.type = type;
        this.points = points;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

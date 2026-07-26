package com.example.midterm_application.data.model;

public class RewardProduct {
    private final int id;
    private final String name;
    private final int imageResId;
    private final int pointCost;

    public RewardProduct(int id, String name, int imageResId, int pointCost) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.pointCost = pointCost;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getPointCost() {
        return pointCost;
    }
}

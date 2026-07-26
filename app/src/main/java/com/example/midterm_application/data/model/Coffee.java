package com.example.midterm_application.data.model;

public class Coffee {
    private final int id;
    private final String name;
    private final int imageResId;
    private final double basePrice;

    public Coffee(int id, String name, int imageResId, double basePrice) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
        this.basePrice = basePrice;
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

    public double getBasePrice() {
        return basePrice;
    }
}

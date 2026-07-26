package com.example.midterm_application.data.repository;

import com.example.midterm_application.R;
import com.example.midterm_application.data.model.RewardProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class RewardCatalog {
    private static final List<RewardProduct> REWARDS = Collections.unmodifiableList(Arrays.asList(
            new RewardProduct(1, "Americano", R.drawable.ic_coffee_cup, 120),
            new RewardProduct(2, "Cappuccino", R.drawable.ic_cup_outline, 160),
            new RewardProduct(3, "Mocha", R.drawable.ic_coffee_cup, 180),
            new RewardProduct(4, "Flat White", R.drawable.ic_cup_outline, 160)
    ));

    private RewardCatalog() {
    }

    public static List<RewardProduct> getRewards() {
        return new ArrayList<>(REWARDS);
    }
}

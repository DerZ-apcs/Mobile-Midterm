package com.example.midterm_application.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoriteRepository {
    private static final String PREFS_NAME = "code_cup_favorites";
    private static final String KEY_FAVORITE_COFFEE_IDS = "favoriteCoffeeIds";

    private final SharedPreferences preferences;

    public FavoriteRepository(Application application) {
        preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public Set<Integer> getFavoriteCoffeeIds() {
        Set<String> persistedIds = preferences.getStringSet(KEY_FAVORITE_COFFEE_IDS, new HashSet<>());
        Set<Integer> favoriteIds = new HashSet<>();
        for (String persistedId : persistedIds) {
            try {
                favoriteIds.add(Integer.parseInt(persistedId));
            } catch (NumberFormatException ignored) {
                // Ignore corrupt values instead of failing Home rendering.
            }
        }
        return favoriteIds;
    }

    public Set<Integer> toggleFavorite(int coffeeId) {
        Set<Integer> favoriteIds = getFavoriteCoffeeIds();
        if (favoriteIds.contains(coffeeId)) {
            favoriteIds.remove(coffeeId);
        } else {
            favoriteIds.add(coffeeId);
        }
        saveFavoriteCoffeeIds(favoriteIds);
        return favoriteIds;
    }

    private void saveFavoriteCoffeeIds(Set<Integer> favoriteIds) {
        Set<String> persistedIds = new HashSet<>();
        for (Integer favoriteId : favoriteIds) {
            persistedIds.add(String.valueOf(favoriteId));
        }
        preferences.edit()
                .putStringSet(KEY_FAVORITE_COFFEE_IDS, persistedIds)
                .apply();
    }
}

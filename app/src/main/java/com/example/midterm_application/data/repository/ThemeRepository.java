package com.example.midterm_application.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class ThemeRepository {
    private static final String PREFS_NAME = "code_cup_theme";
    private static final String KEY_DARK_MODE_ENABLED = "darkModeEnabled";

    private final SharedPreferences preferences;

    public ThemeRepository(Application application) {
        preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isDarkModeEnabled() {
        return preferences.getBoolean(KEY_DARK_MODE_ENABLED, false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        preferences.edit()
                .putBoolean(KEY_DARK_MODE_ENABLED, enabled)
                .apply();
    }
}

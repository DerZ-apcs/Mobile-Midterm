package com.example.midterm_application.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.midterm_application.data.model.UserProfile;

public class ProfileRepository {
    private static final String PREFS_NAME = "code_cup_profile";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";

    private static final String DEFAULT_FULL_NAME = "Anderson";
    private static final String DEFAULT_PHONE = "+60134589525";
    private static final String DEFAULT_EMAIL = "Anderson@email.com";
    private static final String DEFAULT_ADDRESS = "3 Addersion Court\nChino Hills, HO56824, United State";

    private final SharedPreferences preferences;

    public ProfileRepository(Application application) {
        preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public UserProfile getProfile() {
        return new UserProfile(
                preferences.getString(KEY_FULL_NAME, DEFAULT_FULL_NAME),
                preferences.getString(KEY_PHONE, DEFAULT_PHONE),
                preferences.getString(KEY_EMAIL, DEFAULT_EMAIL),
                preferences.getString(KEY_ADDRESS, DEFAULT_ADDRESS));
    }

    public void saveProfile(UserProfile profile) {
        preferences.edit()
                .putString(KEY_FULL_NAME, profile.getFullName())
                .putString(KEY_PHONE, profile.getPhone())
                .putString(KEY_EMAIL, profile.getEmail())
                .putString(KEY_ADDRESS, profile.getAddress())
                .apply();
    }
}

package com.example.midterm_application.viewmodel;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.midterm_application.data.model.UserProfile;
import com.example.midterm_application.data.repository.ProfileRepository;

public class ProfileViewModel extends AndroidViewModel {
    private final ProfileRepository repository;
    private final MutableLiveData<UserProfile> profile = new MutableLiveData<>();
    private final MutableLiveData<ValidationResult> validationResult = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repository = new ProfileRepository(application);
        reloadProfile();
    }

    public LiveData<UserProfile> getProfile() {
        return profile;
    }

    public LiveData<ValidationResult> getValidationResult() {
        return validationResult;
    }

    public void reloadProfile() {
        profile.setValue(repository.getProfile());
    }

    public boolean saveProfile(String fullName, String phone, String email, String address) {
        UserProfile userProfile = new UserProfile(
                trim(fullName),
                trim(phone),
                trim(email),
                trim(address));
        ValidationResult result = validate(userProfile);
        validationResult.setValue(result);
        if (!result.isValid()) {
            return false;
        }

        repository.saveProfile(userProfile);
        profile.setValue(userProfile);
        return true;
    }

    public void clearValidationResult() {
        validationResult.setValue(null);
    }

    private ValidationResult validate(UserProfile userProfile) {
        if (userProfile.getFullName().isEmpty()) {
            return ValidationResult.invalid(Field.FULL_NAME, "Full name cannot be blank");
        }
        if (userProfile.getPhone().isEmpty()) {
            return ValidationResult.invalid(Field.PHONE, "Phone cannot be blank");
        }
        if (userProfile.getEmail().isEmpty()) {
            return ValidationResult.invalid(Field.EMAIL, "Email cannot be blank");
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userProfile.getEmail()).matches()) {
            return ValidationResult.invalid(Field.EMAIL, "Enter a valid email address");
        }
        if (userProfile.getAddress().isEmpty()) {
            return ValidationResult.invalid(Field.ADDRESS, "Address cannot be blank");
        }
        return ValidationResult.valid();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public enum Field {
        NONE,
        FULL_NAME,
        PHONE,
        EMAIL,
        ADDRESS
    }

    public static class ValidationResult {
        private final boolean valid;
        private final Field field;
        private final String message;

        private ValidationResult(boolean valid, Field field, String message) {
            this.valid = valid;
            this.field = field;
            this.message = message;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, Field.NONE, "");
        }

        public static ValidationResult invalid(Field field, String message) {
            return new ValidationResult(false, field, message);
        }

        public boolean isValid() {
            return valid;
        }

        public Field getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}

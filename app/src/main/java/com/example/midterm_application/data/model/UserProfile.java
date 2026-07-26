package com.example.midterm_application.data.model;

public class UserProfile {
    private final String fullName;
    private final String phone;
    private final String email;
    private final String address;

    public UserProfile(String fullName, String phone, String email, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}

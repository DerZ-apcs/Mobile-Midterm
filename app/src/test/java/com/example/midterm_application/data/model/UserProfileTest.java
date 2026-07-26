package com.example.midterm_application.data.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserProfileTest {
    @Test
    public void storesProfileFields() {
        UserProfile profile = new UserProfile(
                "Alex Tan",
                "+60123456789",
                "alex@example.com",
                "12 Coffee Street");

        assertEquals("Alex Tan", profile.getFullName());
        assertEquals("+60123456789", profile.getPhone());
        assertEquals("alex@example.com", profile.getEmail());
        assertEquals("12 Coffee Street", profile.getAddress());
    }
}

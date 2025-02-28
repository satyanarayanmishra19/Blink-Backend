package com.example.blink.dto;

import java.util.Set;

public class UserPreferencesUpdateRequest {
    private String userName;
    private Set<String> preferences;

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Set<String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Set<String> preferences) {
        this.preferences = preferences;
    }
}

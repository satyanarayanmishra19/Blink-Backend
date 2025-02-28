package com.example.blink.dto;

public class UserNumberUpdateRequest {
    private String username;
    private String phoneNumber; 

    // Getters and Setters
    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

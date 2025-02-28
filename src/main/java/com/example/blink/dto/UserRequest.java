package com.example.blink.dto;

public class UserRequest {
    private Long userId;
    private String username;

    // Getter and Setter

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

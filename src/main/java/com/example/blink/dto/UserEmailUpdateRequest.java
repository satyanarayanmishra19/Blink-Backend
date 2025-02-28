package com.example.blink.dto;

import jakarta.validation.constraints.Email;

public class UserEmailUpdateRequest {
    private Long userId;
    private String username;
    @Email
    private String email;;

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public Long getUserId(){
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
}

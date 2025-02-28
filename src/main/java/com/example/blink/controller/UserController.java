package com.example.blink.controller;

import com.example.blink.dto.*;
import com.example.blink.model.User;
import com.example.blink.repository.UserRepository;
import com.example.blink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/save-username")
    public ResponseEntity<String> saveUsername(@RequestBody UserRequest userRequest) {
        userService.saveUserName(userRequest);
        return ResponseEntity.ok("Username saved successfully!");
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody UserDetailsUpdateRequest request) {
        userService.updateUserDetails(request.getUserName(), request.getName(), request.getEmail(),
                request.getPhoneNumber(), request.getCountryCode(), request.getPassword());
        Map<String, Object> response = new HashMap<>();
        response.put("name", request.getName());
        response.put("email", request.getEmail());
        response.put("phone", request.getPhoneNumber());
        response.put("countryCode", request.getCountryCode());
        response.put("password", request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-email")
    public ResponseEntity<Map<String, String>> updateUserEmail(@RequestBody UserEmailUpdateRequest request) {
        try {
            userService.generateEmailUpdateOtp(request.getUserName(), request.getEmail());
            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP sent to new email address.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<Map<String, String>> verifyEmailOtp(@RequestBody EmailOtpVerificationRequest request) {
        try {
            userService.verifyEmailOtp(request.getUserName(), request.getOtp());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email updated successfully.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/update-name")
    public ResponseEntity<Map<String, String>> updateUserName(@RequestBody UserNameUpdateRequest request) {
        try {
            userService.updateUserName(request.getUserName(), request.getName());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Name Updated Successfully.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/update-preferences")
    public ResponseEntity<Map<String, String>> updateUserPreferences(
            @RequestBody UserPreferencesUpdateRequest request) {
        try {
            userService.updateUserPreferences(request.getUserName(), request.getPreferences());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Preferences updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/get-user-details")
    public ResponseEntity<Map<String, String>> getUserDetails(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Map<String, String> userDetails = new HashMap<>();
            userDetails.put("username", user.getUsername());
            userDetails.put("name", user.getName());
            userDetails.put("email", user.getEmail());
            userDetails.put("phone", user.getPhone());
            return ResponseEntity.ok(userDetails);
        }
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "User not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}

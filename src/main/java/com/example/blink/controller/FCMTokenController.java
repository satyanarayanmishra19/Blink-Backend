package com.example.blink.controller;

import com.example.blink.model.User;
import com.example.blink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/fcm")
public class FCMTokenController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/token")
    public ResponseEntity<?> updateFCMToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> tokenRequest) {
        
        String fcmToken = tokenRequest.get("token");
        if (fcmToken == null || fcmToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is required");
        }
        
        User user = userRepository.findByUsername(userDetails.getUsername());
        if (user != null) {
            user.setFcmToken(fcmToken);
            userRepository.save(user);
            return ResponseEntity.ok().body("FCM token updated successfully");
        }
        
        return ResponseEntity.notFound().build();
    }
}
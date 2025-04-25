package com.example.blink.service;

import com.example.blink.model.Message;
import com.example.blink.model.User;
import com.example.blink.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FirebaseNotificationService {

    @Autowired
    private UserRepository userRepository;

    public void sendMessageNotification(Message chatMessage) {
        // Find the user who should receive the notification (the message receiver)
        User receiverOptional = userRepository.findByUsername(chatMessage.getReceiverId());
        
        if (receiverOptional != null) {
            User receiver = receiverOptional;
            String fcmToken = receiver.getFcmToken();
            
            // Only send notification if the user has a FCM token registered
            if (fcmToken != null && !fcmToken.isEmpty()) {
                try {
                    // Find the sender name to show in notification
                    User senderOptional = userRepository.findByUsername(chatMessage.getSenderId());
                    String senderName = senderOptional != null ? 
                            senderOptional.getUsername() : "Someone";
                    
                    // Create notification
                    Notification notification = Notification.builder()
                            .setTitle(senderName)
                            .setBody(chatMessage.getText())
                            .build();
                    
                    // Create message with notification and data payload
                    com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
                            .setToken(fcmToken)
                            .setNotification(notification)
                            .putData("senderId", chatMessage.getSenderId())
                            .putData("messageId", chatMessage.getId().toString())
                            .build();
                    
                    // Send message
                    String response = FirebaseMessaging.getInstance().send(message);
                    System.out.println("Successfully sent message: " + response);
                } catch (FirebaseMessagingException e) {
                    System.err.println("Failed to send Firebase notification: " + e.getMessage());
                }
            }
        }
    }
}
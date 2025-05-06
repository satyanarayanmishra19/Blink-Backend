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
        User receiverOptional = userRepository.findByUsername(chatMessage.getReceiverId());
        
        if (receiverOptional != null) {
            User receiver = receiverOptional;
            String fcmToken = receiver.getFcmToken();
            
            if (fcmToken != null && !fcmToken.isEmpty()) {
                try {
                    User senderOptional = userRepository.findByUsername(chatMessage.getSenderId());
                    String senderName = senderOptional != null ? 
                            senderOptional.getName() : "Someone";
                    
                    Notification notification = Notification.builder()
                            .setTitle(senderName)
                            .setBody(chatMessage.getText())
                            .build();
                    
                    com.google.firebase.messaging.Message message = com.google.firebase.messaging.Message.builder()
                            .setToken(fcmToken)
                            .setNotification(notification)
                            .putData("senderId", chatMessage.getSenderId())
                            .putData("messageId", chatMessage.getId().toString())
                            .build();
                    
                    String response = FirebaseMessaging.getInstance().send(message);
                    System.out.println("Successfully sent message: " + response);
                } catch (FirebaseMessagingException e) {
                    System.err.println("Failed to send Firebase notification: " + e.getMessage());
                }
            }
        }
    }
}
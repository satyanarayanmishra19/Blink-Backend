package com.example.blink.controller;

import com.example.blink.model.ChatMessage;
import com.example.blink.model.User;
import com.example.blink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private UserService userService;

    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{username}")
    public ResponseEntity<List<User>> getChats(@PathVariable String username,
                                               @RequestParam(name = "search", required = false) String search) {
        List<User> users = search != null ? userService.searchUsers(search) : userService.getAllUsers();
        users.removeIf(user -> !userService.hasMatchingPreferences(username, user));
        users.removeIf(user -> user.getUsername().equals(username)); // Remove the requester from the list
        return ResponseEntity.ok(users);
    }

    @MessageMapping("/private-message")
    public void privateMessage(@Payload ChatMessage message) {
        if (message.getSender() == null || message.getRecipient() == null || message.getText() == null) {
            throw new IllegalArgumentException("Sender, recipient, and text are required");
        }

        logger.info("Private message received: " + message);

        String recipient = message.getRecipient();
        String text = message.getText();
        logger.info(String.format("Sending message from %s to %s: %s", message.getSender(), recipient, text));
        simpMessagingTemplate.convertAndSendToUser(recipient, "/private", message);
    }
}
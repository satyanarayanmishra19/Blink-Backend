package com.example.blink.controller;

import com.example.blink.model.ChatMessage;
import com.example.blink.model.User;
import com.example.blink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    @GetMapping("/{username}")
    public ResponseEntity<List<User>> getChats(@PathVariable String username,
                                               @RequestParam(name = "search", required = false) String search) {
        List<User> users = search != null ? userService.searchUsers(search) : userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public ChatMessage receiveMessage(@Payload ChatMessage message) {
        if (message.getSender() == null || message.getText() == null) {
            throw new IllegalArgumentException("Sender and text are required");
        }

        logger.info("Public message received: " + message);

        return message; 
    }

    @MessageMapping("/private-message")
    public void privateMessage(@Payload ChatMessage message) {
        if (message.getSender() == null || message.getRecipient() == null || message.getText() == null) {
            throw new IllegalArgumentException("Sender, recipient, and text are required");
        }

        logger.info("Private message received: " + message);

        String recipient = message.getRecipient();
        String content = message.getText();

        logger.info(String.format("Sending message from %s to %s: %s", message.getSender(), recipient, content));

        simpMessagingTemplate.convertAndSendToUser(recipient, "/private", message);
    }
}
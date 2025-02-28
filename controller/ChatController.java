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

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/{username}")
    public ResponseEntity<List<User>> getChats(@PathVariable String username, @RequestParam(name = "search", required = false) String search) {
        List<User> users = search != null ? userService.searchUsers(search) : userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public ChatMessage receiveMessage(@Payload ChatMessage message) throws InterruptedException {
        if (message.getSender() == null) {
            throw new IllegalArgumentException("Sender not found");
        }
        return message;
    }

    @MessageMapping("/private-message")
    public ChatMessage privateMessage(@Payload ChatMessage message){
        if (message.getRecipient() == null) {
            throw new IllegalArgumentException("Recipient not found");
        }
        System.out.println("Sending Private message to :" + message.getRecipient().getUserName());
        simpMessagingTemplate.convertAndSendToUser(message.getRecipient().getUserName(),"/private",message);
        return message; 
    } 
}

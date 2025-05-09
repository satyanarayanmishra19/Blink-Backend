package com.example.blink.controller;

import com.example.blink.model.Message;
import com.example.blink.repository.MessageRepository;
import com.example.blink.service.FirebaseNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private FirebaseNotificationService firebaseNotificationService;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        Message savedMessage = messageRepository.save(message);
        firebaseNotificationService.sendMessageNotification(savedMessage);
        return savedMessage;
    }

    @GetMapping("/chat/{user1}/{user2}")
    public List<Message> getChatMessages(@PathVariable String user1, @PathVariable String user2) {
        return messageRepository.findChatMessages(user1, user2);
    }
}
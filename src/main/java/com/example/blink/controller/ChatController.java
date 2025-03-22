package com.example.blink.controller;

import com.example.blink.model.User;
import com.example.blink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private UserService userService;

    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    @GetMapping("/{username}")
    public ResponseEntity<List<User>> getChats(@PathVariable String username,
                                               @RequestParam(name = "search", required = false) String search) {
        List<User> users = search != null ? userService.searchUsers(search) : userService.getAllUsers();
        users.removeIf(user -> !userService.hasMatchingPreferences(username, user));
        users.removeIf(user -> user.getUsername().equals(username));
        return ResponseEntity.ok(users);
    }
}
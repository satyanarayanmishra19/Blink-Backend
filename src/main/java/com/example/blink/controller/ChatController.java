package com.example.blink.controller;

import com.example.blink.model.User;
import com.example.blink.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<List<User>> getChats(@PathVariable String username,
                                               @RequestParam(name = "search", required = false) String search) {
        List<User> users = search != null ? userService.searchUsers(search) : userService.getAllUsers();
        users.removeIf(user -> user.getUsername().equals(username));
        return ResponseEntity.ok(users);
    }
}
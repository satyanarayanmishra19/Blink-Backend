package com.example.blink.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private String id;
    private String sender;
    private String recipient;
    private String text;
    private String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "h:mm a")
    private String time;
}
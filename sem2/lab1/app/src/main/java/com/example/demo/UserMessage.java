package com.example.demo;

import java.time.LocalDateTime;

public class UserMessage {

    private final String text;
    private final LocalDateTime publishedAt;

    public UserMessage(String text, LocalDateTime publishedAt) {
        this.text = text;
        this.publishedAt = publishedAt;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
}

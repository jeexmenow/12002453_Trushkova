package com.example.demo;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MessageController {

    private final List<UserMessage> userMessages = new ArrayList<>();

    @GetMapping("/")
    public String helloWorld() {
        return "Hello, World!";
    }

    @GetMapping("/messages")
    public List<UserMessage> getAllMessages() {
        return userMessages;
    }

    @PostMapping("/messages")
    public String publishMessage(@RequestBody String message) {
        userMessages.add(new UserMessage(normalize(message), LocalDateTime.now()));
        return "Message published successfully!";
    }

    @PutMapping("/messages/{index}")
    public String updateMessage(@PathVariable int index, @RequestBody String message) {
        if (index >= 0 && index < userMessages.size()) {
            UserMessage current = userMessages.get(index);
            userMessages.set(index, new UserMessage(normalize(message), current.getPublishedAt()));
            return "Message updated successfully!";
        }
        return "Message not found at index " + index;
    }

    @DeleteMapping("/messages/{index}")
    public String deleteMessage(@PathVariable int index) {
        if (index >= 0 && index < userMessages.size()) {
            userMessages.remove(index);
            return "Message deleted successfully!";
        }
        return "Message not found at index " + index;
    }

    @GetMapping("/messages/count")
    public int countMessages() {
        return userMessages.size();
    }

    @GetMapping("/messages/after")
    public List<UserMessage> getMessagesAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from) {
        List<UserMessage> result = new ArrayList<>();
        for (UserMessage message : userMessages) {
            if (message.getPublishedAt().isAfter(from)) {
                result.add(message);
            }
        }
        return result;
    }

    private String normalize(String message) {
        if (message == null) {
            return "";
        }
        String value = message.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }
}

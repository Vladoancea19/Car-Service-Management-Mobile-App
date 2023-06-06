package com.example.checkit.Models;

public class ChatModel {

    private String chatID, message, sender, timestamp;

    public ChatModel(String chatID, String message, String sender, String timestamp) {
        this.chatID = chatID;
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

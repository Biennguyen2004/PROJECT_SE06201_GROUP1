package com.example.smarthome.data.model;

public class Message {
    public static final String SEND_BY_ME = "me";
    public static final String SEND_BY_BOT = "bot";

    private String message;
    private String sentBy;

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }

    public String getMessage() {
        return message;
    }

    public String getSentBy() {
        return sentBy;
    }
}

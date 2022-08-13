package com.example.TelegramBot.Domains;

public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    BOTH("Both");

    String message;

    Gender(String both) {
        message = both;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

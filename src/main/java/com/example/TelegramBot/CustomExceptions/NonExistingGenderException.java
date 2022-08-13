package com.example.TelegramBot.CustomExceptions;

public class NonExistingGenderException extends Exception{
    public NonExistingGenderException(String message) {
        super(message);
    }
}

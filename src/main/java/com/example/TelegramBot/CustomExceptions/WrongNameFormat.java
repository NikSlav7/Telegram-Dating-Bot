package com.example.TelegramBot.CustomExceptions;

public class WrongNameFormat extends Exception{

    public WrongNameFormat(String message) {
        super(message);
    }

}

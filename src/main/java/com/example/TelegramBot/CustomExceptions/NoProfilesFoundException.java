package com.example.TelegramBot.CustomExceptions;

public class NoProfilesFoundException extends Exception{

    public NoProfilesFoundException(String message){
        super(message);
    }
}

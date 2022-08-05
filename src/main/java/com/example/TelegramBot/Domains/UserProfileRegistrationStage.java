package com.example.TelegramBot.Domains;

import java.awt.desktop.PreferencesEvent;

public enum UserProfileRegistrationStage {
    NO_INFORMATION("Thank you for joining our bot, please fill some details about yourself in order to proceed"),
    NAME_PROVIDED("Your Full Name \n Please follow given format: Name Surname"),
    AGE_PROVIDED("Your birthday? \n Please follow given format dd/mm/yyyy"),
    DEFAULT_LOCATION_PROVIDED("Superb! Please provide us your location through telegram"),
    PHOTO_PROVIDED("Great! It is time for profile photo \n Send your profile photo which will be shown to other users"),
    HOBBIES_PROVIDED("Only few steps left! Please provide your hobbies!"),
    ADDITIONAL_INFO_PROVIDED("Last step! Provide some facts about yourself!"),
    REGISTRATION_COMPLETED("Congrats, you have created your profile!");


    private final String message;
    UserProfileRegistrationStage(String s) {
        message = s;
    }
    public String getMessage() {
        return message;
    }
}

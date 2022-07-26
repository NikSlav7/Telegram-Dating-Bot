package com.example.TelegramBot.MessageSenders;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class RegistrationMessageSender {

    public static final String WELCOME_MESSAGE = "Thank you for joining our bot, please fill some details about yourself in order to proceed";

    public static final String PROVIDE_FULL_NAME = "Your Full Name \n Please follow given format: Name Surname";

    public static final String PROVIDE_AGE= "Your birthday? \n Please follow given format dd/mm/yyyy";

    public static final String PROVIDE_PHOTO = "Great! It is time for profile photo \n Send your profile photo which will be shown to other users";

    public static final String PROVIDE_DEFAULT_LOCATION = "Superb! Please provide us your living city";

    public static final String PROVIDE_ADDITIONAL_INFO = "Last step! Provide some facts about yourself!";

    public static final String PROVIDE_HOBBIES = "Only few steps left! Please provide your hobbies!";

    public static final String DEFAULT_LOCATION_PROVIDED = "Superb! Please provide us your living city";

    public static final String REGISTRATION_COMPLETED = "Congrats, you have created your profile!";







    public static SendMessage sendUserRegistrationMessage(long sendChatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(sendChatId);
        return sendMessage;
    }
}

package com.example.TelegramBot.MessageSenders;


import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class ErrorMessageSender {
    public static final String WRONG_NAME_FORMAT = "Type your first and second name with space between them. Please type your name as here: Elon Musk";

    public static final String WRONG_AGE_FORMAT = "Please include only numbers";

    public static final String TOO_HIGH_AGE = "You can't be that old, please set your real age";

    public static final String NUMBERS_IN_LOCATION_NAME = "Your location should contain only LETTERS!";


    public static SendMessage sendNewErrorMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        return sendMessage;
    }
}

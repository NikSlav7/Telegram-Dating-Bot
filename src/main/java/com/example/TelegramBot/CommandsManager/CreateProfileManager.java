package com.example.TelegramBot.CommandsManager;


import com.example.TelegramBot.ReplyMarkupManager.ProfileCreationMarkupManager;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
public class CreateProfileManager {


    String WELCOME_MESSAGE = "Thank you for joining our bot, please fill some details about yourself in order to proceed";

    public SendMessage startNewProfileCreation(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(WELCOME_MESSAGE);
        sendMessage.setReplyMarkup(ProfileCreationMarkupManager.getProfileCreationDefaultMarkup());
        sendMessage.setChatId(update.getChannelPost().getChatId());
        return sendMessage;
    }
}

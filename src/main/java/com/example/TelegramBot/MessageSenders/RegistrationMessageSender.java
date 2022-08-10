package com.example.TelegramBot.MessageSenders;


import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.ReplyMarkupManager.ProfileCreationMarkupManager;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public class RegistrationMessageSender {



    public static SendMessage sendUserRegistrationMessage(long sendChatId, String message, boolean addMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(sendChatId);

        if (addMarkup) sendMessage.setReplyMarkup(ProfileCreationMarkupManager.getDefaultMarkup());
        return sendMessage;
    }
    public static SendMessage sendNextStepRegistrationMessage(long sendChatId, UserProfileRegistrationStage currentRegistrationStage, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(sendChatId);
        UserProfileRegistrationStage[] stages = UserProfileRegistrationStage.values();
        for (int i = 0; i < stages.length; i++) {
            if (stages[i] == currentRegistrationStage) {
                int pos = i != stages.length - 1 ? i + 1 : i;
                sendMessage.setText(stages[pos].getMessage());
            }
        }
        if (keyboardMarkup != null) sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }
}

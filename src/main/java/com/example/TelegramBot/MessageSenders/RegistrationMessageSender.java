package com.example.TelegramBot.MessageSenders;


import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class RegistrationMessageSender {



    public static SendMessage sendUserRegistrationMessage(long sendChatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(sendChatId);
        return sendMessage;
    }
    public static SendMessage sendNextStepRegistrationMessage(long sendChatId, UserProfileRegistrationStage currentRegistrationStage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(sendChatId);
        UserProfileRegistrationStage[] stages = UserProfileRegistrationStage.values();
        for (int i = 0; i < stages.length; i++) {
            if (stages[i] == currentRegistrationStage) {
                int pos = i != stages.length - 1 ? i + 1 : i;
                sendMessage.setText(stages[pos].getMessage());
            }
        }
        return sendMessage;
    }
}

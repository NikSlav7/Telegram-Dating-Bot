package com.example.TelegramBot;

import com.example.TelegramBot.CommandsManager.CreateProfileManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {

    private final CreateProfileManager createProfileManager;

    public TelegramBot(CreateProfileManager createProfileManager) {
        this.createProfileManager = createProfileManager;
    }

    @Override
    public String getBotUsername() {
        return "Find_Your_Couple_Bot";
    }

    @Override
    public String getBotToken() {
        return "5428249554:AAFBGBgpjBGEe6W9uoOgCqhNzICaU_EdvvA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        String[] message = update.getMessage().getText().split("\\s+");

        //Profile Creation
        if (message[0].equalsIgnoreCase("/createprofile")) {
            SendMessage sendMessage = createProfileManager.startNewProfileCreation(update);
            System.out.println(sendMessage.getText());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

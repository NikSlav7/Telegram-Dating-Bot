package com.example.TelegramBot.TelegramManaging;


import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramService {

    private final TelegramBot telegramBot;


    @Autowired
    public TelegramService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException exception) {
            exception.printStackTrace();
        }
    }
}

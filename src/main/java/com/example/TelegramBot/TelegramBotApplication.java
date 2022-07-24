package com.example.TelegramBot;

import com.example.TelegramBot.CommandsManager.CreateProfileManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TelegramBotApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(TelegramBotApplication.class);
		CreateProfileManager createProfileManager = context.getBean(CreateProfileManager.class);
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new TelegramBot(createProfileManager));
		} catch (TelegramApiException exception) {
			exception.printStackTrace();
		}
	}

}

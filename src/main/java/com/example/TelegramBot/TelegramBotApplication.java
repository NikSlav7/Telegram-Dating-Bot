package com.example.TelegramBot;

import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.TelegramManaging.TelegramBot;
import com.example.TelegramBot.TelegramManaging.TelegramService;
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

	}

}

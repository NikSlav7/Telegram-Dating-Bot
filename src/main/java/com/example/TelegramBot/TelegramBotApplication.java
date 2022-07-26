package com.example.TelegramBot;

import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.Repositories.UserProfileRepository;
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

		UserProfileRepository userProfileRepository = context.getBean(UserProfileRepository.class);

		UserProfileJDBC userProfileJDBC = context.getBean(UserProfileJDBC.class);


		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new TelegramBot( userProfileRepository, userProfileJDBC));
		} catch (TelegramApiException exception) {
			exception.printStackTrace();
		}
	}

}

package com.example.TelegramBot;

import com.example.TelegramBot.AmazonServices.AmazonPicturesManager;
import com.example.TelegramBot.TelegramManaging.TelegramFileDownloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

@SpringBootTest
class TelegramBotApplicationTests {


	@Autowired
	TelegramFileDownloadService telegramFileDownloadService;

	@Autowired
	AmazonPicturesManager amazonPicturesManager;
	@Test
	void contextLoads() throws IOException, InterruptedException {

	}


	@Test
	void downloadTelegramPicture() throws TelegramApiException, IOException, InterruptedException {
		System.out.println(telegramFileDownloadService.downloadFile("AgACAgQAAxkBAAICfmLrlSiatdZOmP_Ye6W8kvGitmONAAKdtjEbUk9gUzJU0jmcS52kAQADAgADcwADKQQ", 1));
	}


}

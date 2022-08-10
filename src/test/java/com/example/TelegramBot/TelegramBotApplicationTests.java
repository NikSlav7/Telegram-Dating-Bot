package com.example.TelegramBot;

import com.example.TelegramBot.AmazonServices.AmazonPicturesManager;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.Domains.WatchedProfile;
import com.example.TelegramBot.IDs.WatchedProfileId;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Repositories.WatchedProfilesRepository;
import com.example.TelegramBot.TelegramManaging.TelegramFileDownloadService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@SpringBootTest
class TelegramBotApplicationTests {


	@Autowired
	TelegramFileDownloadService telegramFileDownloadService;

	@Autowired
	AmazonPicturesManager amazonPicturesManager;

	@Autowired
	UserProfileRepository userProfileRepository;

	@Autowired
	WatchedProfilesRepository watchedProfilesRepository;

	@Autowired
	UserProfileJDBC userProfileJDBC;


	@Test
	void contextLoads() throws IOException, InterruptedException {

	}


	@Test
	void downloadTelegramPicture() throws TelegramApiException, IOException, InterruptedException {
		System.out.println(telegramFileDownloadService.downloadFile("AgACAgQAAxkBAAICfmLrlSiatdZOmP_Ye6W8kvGitmONAAKdtjEbUk9gUzJU0jmcS52kAQADAgADcwADKQQ", 1));
	}


	@Test
	void  getLikedByProfilesTest() {
		UserProfile userProfile = userProfileRepository.getUserProfileById(933602766).get();
		UserProfile userProfile1 = userProfileRepository.getUserProfileById(575849854).get();
		WatchedProfile watchedProfile = new WatchedProfile(new WatchedProfileId(userProfile1.getId(), userProfile.getId()), true, false, false, new Date(System.currentTimeMillis()), userProfile1, userProfile);
		watchedProfilesRepository.save(watchedProfile);
		List<UserProfile> userProfileList =  userProfileJDBC.getLikedByProfiles(userProfile);
		userProfileList.forEach(userProfile2 -> System.out.println(userProfile2.toString()));
		System.out.println(userProfileList.size());
	}

	@Test
	void getAllNearProfilesTest() {
		UserProfile userProfile = userProfileRepository.getUserProfileById(1209815575).get();
		List<UserProfile> userProfileList = userProfileJDBC.getNearestAndClosestByAgeUserProfiles(userProfile, userProfile.getUserDefaultLocation());
		userProfileList.stream().forEach(userProfile1 -> System.out.println(userProfile1.toString()));
		assert userProfileList.size() > 0;
	}




}

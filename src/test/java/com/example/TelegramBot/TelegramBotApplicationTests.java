package com.example.TelegramBot;

import com.amazonaws.services.mturk.model.HIT;
import com.example.TelegramBot.AmazonServices.AmazonPicturesManager;
import com.example.TelegramBot.Domains.RootProfile;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.Domains.WatchedProfile;
import com.example.TelegramBot.IDs.WatchedProfileId;
import com.example.TelegramBot.JDBC.BannedProfileJDBC;
import com.example.TelegramBot.JDBC.RootProfileJDBC;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.Repositories.BannedProfileRepository;
import com.example.TelegramBot.Repositories.RootProfileRepository;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Repositories.WatchedProfilesRepository;
import com.example.TelegramBot.RowMappers.UserProfileRowMapper;
import com.example.TelegramBot.TelegramManaging.TelegramFileDownloadService;
import com.example.TelegramBot.Warnings.BannedProfile;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Commit;
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

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	BannedProfileJDBC bannedProfileJDBC;

	@Autowired
	RootProfileJDBC rootProfileJDBC;

	@Autowired
	BannedProfileRepository bannedProfileRepository;

	@Autowired
	RootProfileRepository rootProfileRepository;


	@Test
	void contextLoads() throws IOException, InterruptedException {

	}


	@Test
	void downloadTelegramPicture() throws TelegramApiException, IOException, InterruptedException {
		System.out.println(telegramFileDownloadService.downloadFile("AgACAgQAAxkBAAICfmLrlSiatdZOmP_Ye6W8kvGitmONAAKdtjEbUk9gUzJU0jmcS52kAQADAgADcwADKQQ", 1));
	}


	@Test
	@Commit
	void  getLikedByProfilesTest() {
		UserProfile userProfile = userProfileRepository.getUserProfileById(1209815575).get();
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

	@Test
	@Commit
	void setRootAndBannedProfilesToExisting(){
		List<UserProfile> listProfile = jdbcTemplate.query("SELECT * FROM user_profile", new UserProfileRowMapper());
		for (UserProfile userProfile : listProfile) {
			RootProfile rootProfile = rootProfileJDBC.createNewRootProfile(userProfile.getId());
			BannedProfile bannedProfile = bannedProfileJDBC.createNewBannedProfile(userProfile.getId());
			rootProfile.setUserProfile(userProfile);
			rootProfile.setBannedProfile(bannedProfile);
			bannedProfile.setRootProfile(rootProfile);
			userProfile.setRootProfile(rootProfile);

			bannedProfileRepository.save(bannedProfile);
			rootProfileRepository.save(rootProfile);
		}
		userProfileRepository.saveAll(listProfile);
	}




}

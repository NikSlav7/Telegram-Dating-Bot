package com.example.TelegramBot.Warnings;


import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Repositories.UserWarningsRepo;
import com.example.TelegramBot.Time.TimesManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class BansManager {


    private final JdbcTemplate jdbcTemplate;

    private final UserProfileRepository userProfileRepository;

    private final UserWarningsRepo userWarningsRepo;

    public BansManager(JdbcTemplate jdbcTemplate, UserProfileRepository userProfileRepository, UserWarningsRepo userWarningsRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.userProfileRepository = userProfileRepository;
        this.userWarningsRepo = userWarningsRepo;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkNeededToBanAccounts() {
        List<Long> ids = jdbcTemplate.queryForList("SELECT count(warning_to_id) FROM user_warning GROUP BY warning_to_id HAVING count(warning_to_id) >= 3", Long.class);;
        List<UserProfile> userProfiles = ids.stream().map(i -> userProfileRepository.getUserProfileById(i).get()).collect(Collectors.toList());
        userProfileRepository.deleteAll(userProfiles);
        banUser(userProfiles);
    }

    private void banUser(List<UserProfile> userProfiles) {
        for (UserProfile profile : userProfiles) {
            int daysBan = determineBanDuration(profile);
            if (checkIfBanForever(profile, daysBan)) continue;
            java.util.Date banUntil = new Date(System.currentTimeMillis() + TimesManager.getDaysInMillis(daysBan));
            profile.setBanned(true);
            profile.increaseBans();
            profile.setBanUntil(banUntil);
        }
        userProfileRepository.saveAll(userProfiles);
    }
    private int determineBanDuration(UserProfile userProfile) {
        switch (userProfile.getTimesBanned()) {
            case 0:
                return 3;
            case 1:
                return 5;
            case 2:
                return 10;
            case 3:
                return 14;

        }
        //means there are more than 3 bans, return -1 and then do check to ban forever
        return -1;
    }
    private boolean checkIfBanForever(UserProfile userProfile, int banDuration) {
        if (banDuration >= 0) return false;
        userProfile.setPermanentlyBanned(true);
        return true;
    }
}

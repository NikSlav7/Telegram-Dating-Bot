package com.example.TelegramBot.Warnings;


import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Repositories.UserWarningsRepository;
import com.example.TelegramBot.Time.TimesManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class BansManager {


    public static final int MAX_BANS = 3;

    private final JdbcTemplate jdbcTemplate;

    private final UserProfileRepository userProfileRepository;

    private final UserWarningsRepository userWarningsRepository;

    public BansManager(JdbcTemplate jdbcTemplate, UserProfileRepository userProfileRepository, UserWarningsRepository userWarningsRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userProfileRepository = userProfileRepository;
        this.userWarningsRepository = userWarningsRepository;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkNeededToBanAccounts() {
        List<Long> ids = jdbcTemplate.queryForList("SELECT warning_to_id FROM user_warning GROUP BY warning_to_id HAVING count(warning_to_id) >= ?", Long.class, MAX_BANS);
        if (ids.size() == 0) return;
        List<UserProfile> userProfiles = ids.stream().map(i -> userProfileRepository.getUserProfileById(i).get()).collect(Collectors.toList());
        String sqlQuery = String.join(",", Collections.nCopies(ids.size(), "?"));
        jdbcTemplate.update(String.format("DELETE FROM user_warning WHERE warning_to_id IN (%s)", sqlQuery), ids.toArray());
        //delete all warnings
        banUsers(userProfiles);
    }

    private void banUsers(List<UserProfile> userProfiles) {
        for (UserProfile profile : userProfiles) {
            int daysBan = determineBanDuration(profile);
            if (checkIfBanForever(profile, daysBan)) continue;
            java.util.Date banUntil = new Date(System.currentTimeMillis() + TimesManager.getDaysInMillis(daysBan));
            profile.getRootProfile().getBannedProfile().increaseBans(1);
            profile.getRootProfile().getBannedProfile().setBanned(true);
            profile.getRootProfile().getBannedProfile().setBannedUntil(banUntil);

        }
        userProfileRepository.saveAll(userProfiles);
    }
    private int determineBanDuration(UserProfile userProfile) {
        switch (userProfile.getRootProfile().getBannedProfile().getTimesBanned()) {
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
        userProfile.getRootProfile().getBannedProfile().setPermanentlyBanned(true);
        userProfileRepository.save(userProfile);
        return true;
    }
}

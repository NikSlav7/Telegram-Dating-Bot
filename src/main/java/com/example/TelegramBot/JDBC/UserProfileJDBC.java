package com.example.TelegramBot.JDBC;


import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.RowMappers.UserProfileRowMapper;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserProfileJDBC {

    private final JdbcTemplate jdbcTemplate;

    private final UserProfileRepository userProfileRepository;

    public UserProfileJDBC(JdbcTemplate jdbcTemplate, UserProfileRepository userProfileRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userProfileRepository = userProfileRepository;
    }


    public boolean checkIfUserProfileExists(long userId) {
        return userProfileRepository.getUserProfileById(userId).isPresent();
    }

    public void createNewUserProfile(long id){
        UserProfile userProfile = new UserProfile();
        userProfile.setId(id);
        userProfile.setProfileRegistrationStage(UserProfileRegistrationStage.NO_INFORMATION);
        userProfileRepository.save(userProfile);
    }

    public List<UserProfile> getNearestAndClosestByAgeUserProfiles(UserProfile searcher, Location location) {
        return jdbcTemplate.query("SELECT * FROM user_profile WHERE user_profile.id != ? AND NOT EXISTS(SELECT * FROM watched_profile WHERE asked_profile_id = ? AND reviewed_profile_id = user_profile.id) AND profile_registration_stage = ? ORDER BY ABS((user_profile.default_longitude - ?)) + ABS((user_profile.default_latitude - ?)) ASC, ABS((extract(year from  user_profile.date_of_birth)) - ?) ASC LIMIT 10"
                ,
                new UserProfileRowMapper(), searcher.getId(), searcher.getId(), 6, location.getLongitude(), location.getLatitude(), LocalDateTime.ofInstant(searcher.getDateOfBirth().toInstant(), ZoneId.systemDefault()).getYear());
    }

    public List<UserProfile> getLikedByProfiles(UserProfile searcher) {
        List<Long> likedByIds = jdbcTemplate.queryForList("SELECT asked_profile_id FROM watched_profile WHERE reviewed_profile_id = ? AND liked_by_one = 't' AND reviewed_by_both_profiles = 'f'",Long.class, searcher.getId());
        return likedByIds.stream().map(l -> userProfileRepository.getUserProfileById(l).get()).collect(Collectors.toList());
    }
}

package com.example.TelegramBot.JDBC;


import com.example.TelegramBot.CustomExceptions.NonExistingGenderException;
import com.example.TelegramBot.Domains.Gender;
import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.RowMappers.UserProfileRowMapper;
import com.example.TelegramBot.TelegramManaging.TelegramBotCommands;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class UserProfileJDBC {

    private final JdbcTemplate jdbcTemplate;

    private final UserProfileRepository userProfileRepository;

    public UserProfileJDBC(JdbcTemplate jdbcTemplate, UserProfileRepository userProfileRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userProfileRepository = userProfileRepository;
    }


    public boolean checkIfUserProfileExists(User user) {
        return userProfileRepository.getUserProfileById(user.getId()).isPresent();
    }

    public void createNewUserProfile(User user){
        UserProfile userProfile = new UserProfile();
        userProfile.setId(user.getId());
        userProfile.setLastInvokedCommand(TelegramBotCommands.START);
        userProfile.setProfileRegistrationStage(UserProfileRegistrationStage.NO_INFORMATION);
        userProfileRepository.save(userProfile);
    }

    public List<UserProfile> getNearestAndClosestByAgeUserProfiles(UserProfile searcher, Location location) {
        return jdbcTemplate.query("SELECT * FROM user_profile WHERE user_profile.id != ? AND NOT EXISTS(SELECT * FROM watched_profile WHERE asked_profile_id = ? AND reviewed_profile_id = user_profile.id) AND profile_registration_stage = ? AND profile_gender = ? ORDER BY calculate_distance(?, ?, default_latitude, default_longitude, 'M') ASC, ABS((extract(year from  user_profile.date_of_birth)) - ?) ASC LIMIT 10"
                ,
                new UserProfileRowMapper(), searcher.getId(), searcher.getId(), UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED.ordinal(), searcher.getSeekingFor().ordinal(),location.getLatitude(), location.getLongitude(), LocalDateTime.ofInstant(searcher.getDateOfBirth().toInstant(), ZoneId.systemDefault()).getYear());
    }

    public List<UserProfile> getLikedByProfiles(UserProfile searcher) {
        List<Long> likedByIds = jdbcTemplate.queryForList("SELECT asked_profile_id FROM watched_profile WHERE reviewed_profile_id = ? AND liked_by_one = 't' AND reviewed_by_both_profiles = 'f'",Long.class, searcher.getId());
        return likedByIds.stream().map(l -> userProfileRepository.getUserProfileById(l).get()).collect(Collectors.toList());
    }

    public UserProfile setUserProfileGenderAndSave(UserProfile userProfile, String gender) throws NonExistingGenderException {
        if (!gender.equalsIgnoreCase(Gender.MALE.getMessage()) && !gender.equalsIgnoreCase(Gender.FEMALE.getMessage())) throw new NonExistingGenderException("No such gender");

        if (gender.equalsIgnoreCase(Gender.MALE.getMessage())) {
            userProfile.setProfileGender(Gender.MALE);
        }
        else userProfile.setProfileGender(Gender.FEMALE);
        return userProfileRepository.save(userProfile);
    }

    public UserProfile setSeekingForGenderAndSave(UserProfile userProfile, String gender) throws NonExistingGenderException {
        if (!gender.equalsIgnoreCase(Gender.MALE.getMessage()) && !gender.equalsIgnoreCase(Gender.FEMALE.getMessage()) && !gender.equalsIgnoreCase(Gender.BOTH.getMessage()))
            throw new NonExistingGenderException("No such gender");

        if (gender.equalsIgnoreCase(Gender.MALE.getMessage())) {
            userProfile.setSeekingFor(Gender.MALE);
        }
        else if (gender.equalsIgnoreCase(Gender.FEMALE.getMessage())) userProfile.setSeekingFor(Gender.FEMALE);
        else userProfile.setSeekingFor(Gender.BOTH);
        return userProfileRepository.save(userProfile);
    }


    //Return null if user is not banned
    public java.util.Date  getUserBannedUntilDate(UserProfile userProfile) {
        return userProfile.isBanned() ? null : userProfile.getBanUntil();
    }
}

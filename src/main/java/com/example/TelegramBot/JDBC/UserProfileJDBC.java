package com.example.TelegramBot.JDBC;


import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
}

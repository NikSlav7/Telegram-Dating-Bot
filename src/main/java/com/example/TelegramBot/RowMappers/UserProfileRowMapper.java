package com.example.TelegramBot.RowMappers;

import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.ProfileSeekingMode;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import net.dv8tion.jda.api.entities.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserProfileRowMapper implements RowMapper<UserProfile> {
    @Override
    public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(rs.getLong("id"));
        userProfile.setAdditionalInfo(rs.getString("additional_info"));
        userProfile.setDateOfBirth(rs.getDate("date_of_birth"));
        userProfile.setFirstName(rs.getString("first_name"));
        userProfile.setHobbies(rs.getString("hobbies"));
        userProfile.setLastInvokedCommand("last_invoked_command");
        userProfile.setProfilePictureLink(rs.getString("profile_picture_link"));
        userProfile.setProfileRegistrationStage(UserProfileRegistrationStage.values()[rs.getInt("profile_registration_stage")]);
        userProfile.setProfileSeekingMode(ProfileSeekingMode.values()[rs.getInt("profile_seeking_mode")]);
        userProfile.setSecondName(rs.getString("second_name"));
        userProfile.setUserDefaultLocation(new Location(rs.getDouble("default_latitude"), rs.getDouble("default_longitude"), rs.getString("default_country_name"),
                rs.getString("default_region_name"), rs.getString("default_city_name")));
        userProfile.setUserLatestLocation(new Location(rs.getDouble("latest_latitude"), rs.getDouble("latest_longitude"), rs.getString("latest_country_name"),
                rs.getString("latest_region_name"), rs.getString("latest_city_name")));
        userProfile.setCommandToConfirm(rs.getString("command_to_confirm"));
        return userProfile;
    }
}

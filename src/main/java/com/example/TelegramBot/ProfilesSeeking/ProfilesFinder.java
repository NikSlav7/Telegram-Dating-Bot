package com.example.TelegramBot.ProfilesSeeking;


import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.IDs.WatchedProfileId;
import com.example.TelegramBot.RowMappers.UserProfileRowMapper;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfilesFinder {


    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public ProfilesFinder(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    public UserProfile findAppropriateProfile(UserProfile searcher){
        Location location = null;
        switch (searcher.getProfileSeekingMode()) {
            case SEEKING_BY_LATEST_LOCATION -> location = searcher.getUserLatestLocation();
            case SEEKING_BY_DEFAULT_LOCATION -> location = searcher.getUserDefaultLocation();
        }
        List<UserProfile> profiles = null;
        while ((profiles = getUserProfiles(searcher, location)).stream().filter(userProfile -> !searcher.getAskedProfiles().contains(new WatchedProfileId(searcher.getId(), userProfile.getId()))).collect(Collectors.toList()).size() == 0) {
        }
        return profiles.get(0);
    }

    private List<UserProfile> getUserProfiles(UserProfile searcher, Location location) {
        return jdbcTemplate.query("SELECT * FROM user_profile ORDER BY ABS((user_profile.default_longitude - ?)) + ABS((user_profile.default_latitude - ?)) DESC, ABS((extract(year from  user_profile.date_of_birth)) - ?) DESC LIMIT 100",
                new UserProfileRowMapper(), location.getLongitude(), location.getLatitude(), LocalDateTime.ofInstant(searcher.getDateOfBirth().toInstant(), ZoneId.systemDefault()).getYear());
    }


}

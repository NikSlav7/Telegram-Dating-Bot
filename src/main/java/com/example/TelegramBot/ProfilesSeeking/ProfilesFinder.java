package com.example.TelegramBot.ProfilesSeeking;


import com.example.TelegramBot.CustomExceptions.NoProfilesFoundException;
import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.ProfileSeekingMode;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Repositories.WatchedProfilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProfilesFinder {


    private final JdbcTemplate jdbcTemplate;

    private final UserProfileRepository userProfileRepository;

    private final WatchedProfilesRepository watchedProfilesRepository;

    private final UserProfileJDBC userProfileJDBC;


    @Autowired
    public ProfilesFinder(JdbcTemplate jdbcTemplate, UserProfileRepository userProfileRepository, WatchedProfilesRepository watchedProfilesRepository, UserProfileJDBC userProfileJDBC) {
        this.jdbcTemplate = jdbcTemplate;
        this.userProfileRepository = userProfileRepository;
        this.watchedProfilesRepository = watchedProfilesRepository;
        this.userProfileJDBC = userProfileJDBC;
    }



    public UserProfile findAppropriateProfile(UserProfile searcher) throws Exception {
        Location location = searcher.getProfileSeekingMode() == ProfileSeekingMode.SEEKING_BY_DEFAULT_LOCATION ? searcher.getUserDefaultLocation() : searcher.getUserLatestLocation();
        List<UserProfile> profiles = userProfileJDBC.getNearestAndClosestByAgeUserProfiles(searcher, location);
        setUserCurrentlyReviewingProfile(searcher, profiles.get(0));
        return profiles.get(0);
    }

    public UserProfile findLikedByProfiles(UserProfile searcher) throws NoProfilesFoundException {
        List<UserProfile> profiles = userProfileJDBC.getLikedByProfiles(searcher);
        if (profiles.size() == 0) throw new NoProfilesFoundException("No appropriate profiles were found");
        searcher.setCurrentReviewingProfileId(profiles.get(0).getId());
        return profiles.get(0);
    }



    public void setUserCurrentlyReviewingProfile(UserProfile searcher, UserProfile appropriateProfile) {
        searcher.setCurrentReviewingProfileId(appropriateProfile.getId());
    }

}

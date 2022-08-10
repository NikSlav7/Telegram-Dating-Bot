package com.example.TelegramBot.ProfilesSeeking;

import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.WatchedProfile;
import com.example.TelegramBot.IDs.WatchedProfileId;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Repositories.WatchedProfilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;


@Component
public class ReviewedAccountsManager {


    private final UserProfileRepository userProfileRepository;

    private final WatchedProfilesRepository watchedProfilesRepository;



    @Autowired
    public ReviewedAccountsManager(UserProfileRepository userProfileRepository, WatchedProfilesRepository watchedProfilesRepository) {
        this.userProfileRepository = userProfileRepository;
        this.watchedProfilesRepository = watchedProfilesRepository;
    }


    public void createReviewedAccount(UserProfile searcher, boolean liked) {
        UserProfile reviewedProfile = userProfileRepository.getUserProfileById(searcher.getCurrentReviewingProfileId()).get();
        WatchedProfile watchedProfile = new WatchedProfile();
        watchedProfile.setWatchedProfileId(new WatchedProfileId(searcher.getId(), reviewedProfile.getId()));
        watchedProfile.setAskedUserProfile(searcher);
        watchedProfile.setReviewedUserProfile(reviewedProfile);
        watchedProfile.setReviewedByBothProfiles(false);
        watchedProfile.setLikedByBoth(false);
        watchedProfile.setProfileReviewTime(new Date(System.currentTimeMillis()));
        watchedProfile.setLikedByOne(liked);
        reviewedProfile.getReviewedBy().add(watchedProfile);
        searcher.getAskedProfiles().add(watchedProfile);

        watchedProfilesRepository.save(watchedProfile);
        userProfileRepository.saveAll(List.of(searcher, reviewedProfile));
    }
}

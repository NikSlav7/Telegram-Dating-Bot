package com.example.TelegramBot.Domains;


import com.example.TelegramBot.IDs.WatchedProfileId;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class WatchedProfile{

    @EmbeddedId
    WatchedProfileId watchedProfileId;


    //either has the account liked or not the profile
    boolean likedByOne;

    boolean likedByBoth;

    //has the profile been shown to both users
    boolean reviewedByBothProfiles;

    @Temporal(TemporalType.TIMESTAMP)
    java.util.Date profileReviewTime;

    @ManyToOne(fetch = FetchType.LAZY)
    UserProfile askedUserProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    UserProfile reviewedUserProfile;


    public WatchedProfile(WatchedProfileId watchedProfileId, boolean likedByOne, boolean likedByBoth, boolean reviewedByBothProfiles, Date profileReviewTime, UserProfile askedUserProfile, UserProfile reviewedUserProfile) {
        this.watchedProfileId = watchedProfileId;
        this.likedByOne = likedByOne;
        this.likedByBoth = likedByBoth;
        this.reviewedByBothProfiles = reviewedByBothProfiles;
        this.profileReviewTime = profileReviewTime;
        this.askedUserProfile = askedUserProfile;
        this.reviewedUserProfile = reviewedUserProfile;
    }

    public WatchedProfile() {
    }

    public WatchedProfileId getWatchedProfileId() {
        return watchedProfileId;
    }

    public void setWatchedProfileId(WatchedProfileId watchedProfileId) {
        this.watchedProfileId = watchedProfileId;
    }

    public boolean isLiked() {
        return likedByOne;
    }

    public void setLiked(boolean liked) {
        this.likedByOne = liked;
    }

    public boolean isReviewedByBothProfiles() {
        return reviewedByBothProfiles;
    }

    public void setReviewedByBothProfiles(boolean reviewedByBothProfiles) {
        this.reviewedByBothProfiles = reviewedByBothProfiles;
    }



    public UserProfile getAskedUserProfile() {
        return askedUserProfile;
    }

    public void setAskedUserProfile(UserProfile askedUserProfile) {
        this.askedUserProfile = askedUserProfile;
    }

    public UserProfile getReviewedUserProfile() {
        return reviewedUserProfile;
    }

    public void setReviewedUserProfile(UserProfile reviewedUserProfile) {
        this.reviewedUserProfile = reviewedUserProfile;
    }

    public java.util.Date getProfileReviewTime() {
        return profileReviewTime;
    }

    public void setProfileReviewTime(java.util.Date profileReviewTime) {
        this.profileReviewTime = profileReviewTime;
    }

    public boolean isLikedByOne() {
        return likedByOne;
    }

    public void setLikedByOne(boolean likedByOne) {
        this.likedByOne = likedByOne;
    }

    public boolean isLikedByBoth() {
        return likedByBoth;
    }

    public void setLikedByBoth(boolean likedByBoth) {
        this.likedByBoth = likedByBoth;
    }
}

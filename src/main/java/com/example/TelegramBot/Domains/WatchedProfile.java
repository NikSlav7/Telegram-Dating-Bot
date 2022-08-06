package com.example.TelegramBot.Domains;


import com.example.TelegramBot.IDs.WatchedProfileId;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class WatchedProfile{

    @EmbeddedId
    WatchedProfileId watchedProfileId;


    //either has the account liked or not the profile
    boolean liked;

    //has the profile been shown to other user
    boolean reviewedByReviewedProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    UserProfile askedUserProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    UserProfile reviewedUserProfile;


    public WatchedProfile(WatchedProfileId watchedProfileId, boolean liked, boolean reviewedByReviewedProfile) {
        super();
        this.watchedProfileId = watchedProfileId;
        this.liked = liked;
        this.reviewedByReviewedProfile = reviewedByReviewedProfile;
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
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isReviewedByReviewedProfile() {
        return reviewedByReviewedProfile;
    }

    public void setReviewedByReviewedProfile(boolean reviewedByReviewedProfile) {
        this.reviewedByReviewedProfile = reviewedByReviewedProfile;
    }
}

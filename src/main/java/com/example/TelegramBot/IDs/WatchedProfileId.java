package com.example.TelegramBot.IDs;


import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.WatchedProfile;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WatchedProfileId implements Serializable {


    long askedProfileId;

    long reviewedProfileId;

    public WatchedProfileId() {
    }

    public WatchedProfileId(long askedProfileId, long reviewedProfileId) {
        this.askedProfileId = askedProfileId;
        this.reviewedProfileId = reviewedProfileId;
    }

    public long getAskedProfileId() {
        return askedProfileId;
    }

    public void setAskedProfileId(long askedProfileId) {
        this.askedProfileId = askedProfileId;
    }

    public long getReviewedProfileId() {
        return reviewedProfileId;
    }

    public void setReviewedProfileId(long reviewedProfileId) {
        this.reviewedProfileId = reviewedProfileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WatchedProfileId that = (WatchedProfileId) o;
        return askedProfileId == that.askedProfileId && reviewedProfileId == that.reviewedProfileId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(askedProfileId, reviewedProfileId);
    }
}

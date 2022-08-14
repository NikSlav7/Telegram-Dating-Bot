package com.example.TelegramBot.Domains;

import com.example.TelegramBot.Warnings.BannedProfile;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table
public class RootProfile extends Profile{

    @Id
    private long id;


    @OneToOne(cascade = CascadeType.ALL)
    private BannedProfile bannedProfile;

    @OneToOne(cascade = CascadeType.ALL)
    private UserProfile userProfile;


    public RootProfile(long id) {
        super(id);
        this.id = id;
    }

    public RootProfile() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BannedProfile getBannedProfile() {
        return bannedProfile;
    }

    public void setBannedProfile(BannedProfile bannedProfile) {
        this.bannedProfile = bannedProfile;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }


}

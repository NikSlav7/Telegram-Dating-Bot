package com.example.TelegramBot.Warnings;

import com.example.TelegramBot.Domains.Profile;
import com.example.TelegramBot.Domains.RootProfile;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table
public class BannedProfile extends Profile {

    @Id
    private long id;

    private int timesBanned;

    private boolean isBanned;


    private boolean isPermanentlyBanned;


    private java.util.Date bannedUntil;




    @OneToOne(mappedBy = "bannedProfile", cascade = CascadeType.ALL)
    private RootProfile rootProfile;


    public BannedProfile(long id, int timesBanned, boolean isBanned, boolean isPermanentlyBanned, Date bannedUntil, RootProfile rootProfile) {
        super(id);
        this.id = id;
        this.timesBanned = timesBanned;
        this.isBanned = isBanned;
        this.isPermanentlyBanned = isPermanentlyBanned;
        this.bannedUntil = bannedUntil;
        this.rootProfile = rootProfile;
    }

    public BannedProfile(long id) {
        super(id);
        this.id = id;
    }

    public BannedProfile() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public boolean isPermanentlyBanned() {
        return isPermanentlyBanned;
    }

    public void setPermanentlyBanned(boolean permanentlyBanned) {
        isPermanentlyBanned = permanentlyBanned;
    }

    public Date getBannedUntil() {
        return bannedUntil;
    }

    public void setBannedUntil(Date bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    public RootProfile getRootProfile() {
        return rootProfile;
    }

    public void setRootProfile(RootProfile rootProfile) {
        this.rootProfile = rootProfile;
    }

    public int getTimesBanned() {
        return timesBanned;
    }

    public void setTimesBanned(int timesBanned) {
        this.timesBanned = timesBanned;
    }
    public void increaseBans(int increaseBy){timesBanned += increaseBy;}
}

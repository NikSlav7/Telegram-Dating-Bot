package com.example.TelegramBot.Warnings;


import com.example.TelegramBot.Domains.UserProfile;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table
public class UserWarning {


    //The id will be UUID, but converted to String;
    @Id
    String id;

    Date creationDate;


    @ManyToOne(fetch = FetchType.LAZY)
    UserProfile warningTo;


    @ManyToOne(fetch = FetchType.LAZY)
    UserProfile warningFrom;


    public UserWarning() {
    }

    public UserWarning(String id, Date creationDate, UserProfile warningTo, UserProfile warningFrom) {
        this.id = id;
        this.creationDate = creationDate;
        this.warningTo = warningTo;
        this.warningFrom = warningFrom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public UserProfile getWarningTo() {
        return warningTo;
    }

    public void setWarningTo(UserProfile warningTo) {
        this.warningTo = warningTo;
    }

    public UserProfile getWarningFrom() {
        return warningFrom;
    }

    public void setWarningFrom(UserProfile warningFrom) {
        this.warningFrom = warningFrom;
    }
}

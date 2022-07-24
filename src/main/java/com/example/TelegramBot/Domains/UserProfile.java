package com.example.TelegramBot.Domains;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Table
@Entity
public class UserProfile extends Profile{

    @Id
    String id;

    String firstName;

    String secondName;

    String hobbies;

    @Column(columnDefinition = "TEXT")
    String additionalInfo;

    String profilePictureLink;

    Date dateOfBirth;

    public UserProfile(String id, String firstName, String secondName, String hobbies, String additionalInfo, String profilePictureLink, Date dateOfBirth) {
        super(firstName, secondName, dateOfBirth);
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.hobbies = hobbies;
        this.additionalInfo = additionalInfo;
        this.profilePictureLink = profilePictureLink;
        this.dateOfBirth = dateOfBirth;
    }


    public UserProfile() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}

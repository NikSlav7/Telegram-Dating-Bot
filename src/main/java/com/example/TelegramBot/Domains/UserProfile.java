package com.example.TelegramBot.Domains;


import javax.persistence.*;
import java.sql.Date;

@Table
@Entity
public class UserProfile extends Profile{

    @Id
    long id;

    String firstName;

    String secondName;

    String hobbies;

    @Column(columnDefinition = "TEXT")
    String additionalInfo;

    String profilePictureLink;

    String userShownLocation;


    String userDefaultLocation;

    String userLatestLocation;

    String lastInvokedCommand;

    @Enumerated(value = EnumType.ORDINAL)
    UserProfileRegistrationStage profileRegistrationStage;

    java.util.Date dateOfBirth;



    public UserProfile(long id, String firstName, String secondName, String hobbies, String additionalInfo, String profilePictureLink, Date dateOfBirth,  String userDefaultLocation, String userLatestLocation,
    String userShownLocation, String lastInvokedCommand, UserProfileRegistrationStage profileRegistrationStage) {
        super(firstName, secondName, dateOfBirth);
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.hobbies = hobbies;
        this.additionalInfo = additionalInfo;
        this.profilePictureLink = profilePictureLink;
        this.dateOfBirth = dateOfBirth;
        this.userDefaultLocation = userDefaultLocation;
        this.userLatestLocation = userLatestLocation;
        this.userShownLocation = userShownLocation;
        this.lastInvokedCommand = lastInvokedCommand;
        this.profileRegistrationStage = profileRegistrationStage;
    }


    public UserProfile() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public java.util.Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.util.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserShownLocation() {
        return userShownLocation;
    }

    public void setUserShownLocation(String userShownLocation) {
        this.userShownLocation = userShownLocation;
    }

    public String getUserDefaultLocation() {
        return userDefaultLocation;
    }

    public void setUserDefaultLocation(String userDefaultLocation) {
        this.userDefaultLocation = userDefaultLocation;
    }

    public String getUserLatestLocation() {
        return userLatestLocation;
    }

    public void setUserLatestLocation(String userLatestLocation) {
        this.userLatestLocation = userLatestLocation;
    }

    public String getLastInvokedCommand() {
        return lastInvokedCommand;
    }

    public void setLastInvokedCommand(String lastInvokedCommand) {
        this.lastInvokedCommand = lastInvokedCommand;
    }

    public UserProfileRegistrationStage getProfileRegistrationStage() {
        return profileRegistrationStage;
    }

    public void setProfileRegistrationStage(UserProfileRegistrationStage profileRegistrationStage) {
        this.profileRegistrationStage = profileRegistrationStage;
    }
}

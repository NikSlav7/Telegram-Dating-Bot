package com.example.TelegramBot.Domains;


import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Table
@Entity
public class UserProfile{

    @Id
    long id;

    String firstName;

    String secondName;

    String hobbies;

    @Column(columnDefinition = "TEXT")
    String additionalInfo;

    String profilePictureLink;



    //profiles that were offered to user
    @OneToMany(mappedBy = "askedUserProfile")
    Set<WatchedProfile> askedProfiles;

    //profiles that this profile was offered to
    @OneToMany(mappedBy = "reviewedUserProfile")
    Set<WatchedProfile> reviewedBy;



    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "default_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "default_longitude")),
            @AttributeOverride(name = "countryName", column = @Column(name = "default_country_name")),
            @AttributeOverride(name = "regionName", column = @Column(name = "default_region_name")),
            @AttributeOverride(name = "cityName", column = @Column(name = "default_city_name")),

    })
    @Embedded
    Location userDefaultLocation;


    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "latest_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "latest_longitude")),
            @AttributeOverride(name = "countryName", column = @Column(name = "latest_country_name")),
            @AttributeOverride(name = "regionName", column = @Column(name = "latest_region_name")),
            @AttributeOverride(name = "cityName", column = @Column(name = "latest_city_name")),
    })
    @Embedded
    Location userLatestLocation;

    String lastInvokedCommand;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    UserProfileRegistrationStage profileRegistrationStage;


    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    ProfileSeekingMode profileSeekingMode;

    java.util.Date dateOfBirth;



    public UserProfile(long id, String firstName, String secondName, String hobbies, String additionalInfo, String profilePictureLink, Date dateOfBirth,  Location userDefaultLocation, Location userLatestLocation, String lastInvokedCommand, UserProfileRegistrationStage profileRegistrationStage) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.hobbies = hobbies;
        this.additionalInfo = additionalInfo;
        this.profilePictureLink = profilePictureLink;
        this.dateOfBirth = dateOfBirth;
        this.userDefaultLocation = userDefaultLocation;
        this.userLatestLocation = userLatestLocation;
        this.lastInvokedCommand = lastInvokedCommand;
        this.profileRegistrationStage = profileRegistrationStage;
        profileSeekingMode = ProfileSeekingMode.NOT_SEEKING;

    }


    public UserProfile() {
        profileSeekingMode = ProfileSeekingMode.NOT_SEEKING;
        profileRegistrationStage = UserProfileRegistrationStage.NO_INFORMATION;
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


    public Location getUserDefaultLocation() {
        return userDefaultLocation;
    }

    public void setUserDefaultLocation(Location userDefaultLocation) {
        this.userDefaultLocation = userDefaultLocation;
    }

    public Location getUserLatestLocation() {
        return userLatestLocation;
    }

    public void setUserLatestLocation(Location userLatestLocation) {
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

    public void resetProfile(){
        UserProfile profile = new UserProfile();
        firstName = null;
        secondName = null;
        hobbies = null;
        additionalInfo = null;
        profilePictureLink = null;
        userDefaultLocation = null;
        userLatestLocation = null;
        profileRegistrationStage = UserProfileRegistrationStage.NO_INFORMATION;
        dateOfBirth = null;
    }

    public Set<WatchedProfile> getAskedProfiles() {
        return askedProfiles;
    }

    public void setAskedProfiles(Set<WatchedProfile> askedProfiles) {
        this.askedProfiles = askedProfiles;
    }

    public Set<WatchedProfile> getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Set<WatchedProfile> reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public ProfileSeekingMode getProfileSeekingMode() {
        return profileSeekingMode;
    }

    public void setProfileSeekingMode(ProfileSeekingMode profileSeekingMode) {
        this.profileSeekingMode = profileSeekingMode;
    }

    public void setUserLatestLocation(org.telegram.telegrambots.meta.api.objects.Location location) {
        if (userLatestLocation == null) userLatestLocation = new Location();
        userLatestLocation.setLatitude(location.getLatitude());
        userLatestLocation.setLongitude(location.getLongitude());
    }
}

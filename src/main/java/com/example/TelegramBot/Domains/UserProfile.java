package com.example.TelegramBot.Domains;


import com.example.TelegramBot.Warnings.UserWarning;

import javax.persistence.*;
import java.util.*;

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

    String commandToConfirm;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    UserProfileRegistrationStage profileRegistrationStage;


    @Enumerated(value = EnumType.ORDINAL)
    @Column(nullable = false)
    ProfileSeekingMode profileSeekingMode;


    java.util.Date dateOfBirth;

    long currentReviewingProfileId;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "warningFrom")
    List<UserWarning> warningsIssued;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "warningTo")
    List<UserWarning> warningsReceived;


    public UserProfile(long id, String firstName, String secondName, String hobbies, String additionalInfo, String profilePictureLink, Set<WatchedProfile> askedProfiles, Set<WatchedProfile> reviewedBy, Location userDefaultLocation, Location userLatestLocation, String lastInvokedCommand, String commandToConfirm, UserProfileRegistrationStage profileRegistrationStage, ProfileSeekingMode profileSeekingMode, Date dateOfBirth, long currentReviewingProfileId, List<UserWarning> warningsIssued, List<UserWarning> warningsReceived) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.hobbies = hobbies;
        this.additionalInfo = additionalInfo;
        this.profilePictureLink = profilePictureLink;
        this.askedProfiles = askedProfiles;
        this.reviewedBy = reviewedBy;
        this.userDefaultLocation = userDefaultLocation;
        this.userLatestLocation = userLatestLocation;
        this.lastInvokedCommand = lastInvokedCommand;
        this.commandToConfirm = commandToConfirm;
        this.profileRegistrationStage = profileRegistrationStage;
        this.profileSeekingMode = profileSeekingMode;
        this.dateOfBirth = dateOfBirth;
        this.currentReviewingProfileId = currentReviewingProfileId;
        this.warningsIssued = warningsIssued;
        this.warningsReceived = warningsReceived;
    }

    public UserProfile() {
        profileSeekingMode = ProfileSeekingMode.NOT_SEEKING;
        profileRegistrationStage = UserProfileRegistrationStage.NO_INFORMATION;
        askedProfiles = new HashSet<>();
        reviewedBy = new HashSet<>();
        warningsIssued = new ArrayList<>();

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

    public long getCurrentReviewingProfileId() {
        return currentReviewingProfileId;
    }

    public void setCurrentReviewingProfileId(long currentReviewingProfileId) {
        this.currentReviewingProfileId = currentReviewingProfileId;
    }

    public String getCommandToConfirm() {
        return commandToConfirm;
    }

    public void setCommandToConfirm(String commandToConfirm) {
        this.commandToConfirm = commandToConfirm;
    }

    public List<UserWarning> getWarningsIssued() {
        return warningsIssued;
    }

    public void setWarningsIssued(List<UserWarning> warningsIssued) {
        this.warningsIssued = warningsIssued;
    }

    public List<UserWarning> getWarningsReceived() {
        return warningsReceived;
    }

    public void setWarningsReceived(List<UserWarning> warningsReceived) {
        this.warningsReceived = warningsReceived;
    }
}

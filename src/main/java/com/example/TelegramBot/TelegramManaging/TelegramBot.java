package com.example.TelegramBot.TelegramManaging;

import com.example.TelegramBot.AmazonServices.AmazonPicturesManager;
import com.example.TelegramBot.CustomExceptions.NoProfilesFoundException;
import com.example.TelegramBot.CustomExceptions.NonExistingGenderException;
import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.ProfileSeekingMode;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.LocationServices.PreciseLocationManager;
import com.example.TelegramBot.MessageSenders.CommandConfirmationMessageSender;
import com.example.TelegramBot.MessageSenders.ErrorMessageSender;
import com.example.TelegramBot.MessageSenders.ProfileInfoSender;
import com.example.TelegramBot.MessageSenders.RegistrationMessageSender;
import com.example.TelegramBot.ProfileRegistration.ProfileRegistrationChecks;
import com.example.TelegramBot.ProfilesSeeking.ProfilesFinder;
import com.example.TelegramBot.ProfilesSeeking.ReviewedAccountsManager;
import com.example.TelegramBot.ProfileRegistration.RegistrationStagesManager;
import com.example.TelegramBot.ReplyMarkupManager.CommandConfirmationMarkupManager;
import com.example.TelegramBot.ReplyMarkupManager.ProfileCreationMarkupManager;
import com.example.TelegramBot.ReplyMarkupManager.ProfilesSeekingMarkupManager;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Warnings.WarningsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


@Component
public class TelegramBot extends TelegramLongPollingBot {


    private final UserProfileRepository userProfileRepository;

    private final UserProfileJDBC userProfileJDBC;

    private final TelegramFileDownloadService telegramFileDownloadService;

    private final AmazonPicturesManager amazonPicturesManager;

    private final PreciseLocationManager preciseLocationManager;

    private final ProfilesFinder profilesFinder;

    private final ReviewedAccountsManager reviewedAccountsManager;

    private final WarningsManager warningsManager;

    private final Clock clock;

    @Autowired
    public TelegramBot(UserProfileRepository userProfileRepository, UserProfileJDBC userProfileJDBC, AmazonPicturesManager amazonPicturesManager, PreciseLocationManager preciseLocationManager, ProfilesFinder profilesFinder, ReviewedAccountsManager reviewedAccountsManager, WarningsManager warningsManager, Clock clock) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileJDBC = userProfileJDBC;
        this.amazonPicturesManager = amazonPicturesManager;
        this.preciseLocationManager = preciseLocationManager;
        this.profilesFinder = profilesFinder;
        this.reviewedAccountsManager = reviewedAccountsManager;
        this.warningsManager = warningsManager;
        this.clock = clock;
        telegramFileDownloadService = new TelegramFileDownloadService(this);

    }

    @Override
    public String getBotUsername() {
        return "Find_Your_Couple_Bot";
    }

    @Override
    public String getBotToken() {
        return "5428249554:AAFBGBgpjBGEe6W9uoOgCqhNzICaU_EdvvA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        String[] message;
        //returning if the message is from bot
        if (update.getMessage().getFrom().getIsBot()) return;

        if (update.getMessage().getText() != null) message = update.getMessage().getText().split("\\s+");
        else message = new String[1];

        //Creating the empty user profile for new user
        if (!userProfileJDBC.checkIfUserProfileExists(update.getMessage().getFrom())) {
            userProfileJDBC.createNewUserProfile(update.getMessage().getFrom());
        }

        //Getting the new user
        UserProfile commandExecutor = userProfileRepository.getUserProfileById(update.getMessage().getChatId()).get();
        java.util.Date banUntil;
        if ((banUntil = userProfileJDBC.getUserBannedUntilDate(commandExecutor)) == null) {

        }

        //That's temporary line of code


        //The new command executor id and the chat id. They are now the same, seperated for the code clearance
        long commandExecutorId = commandExecutor.getId();
        long chatId = update.getMessage().getChatId();


        //Checking if there is any command that user has now to confirm;
       if (commandExecutor.getCommandToConfirm() != null && update.getMessage().getText() != null) {
            boolean confirm = update.getMessage().getText().equals(CommandConfirmationMarkupManager.COMMAND_CONFIRM);
            try {
                performConfirmedCommand(chatId, commandExecutor, confirm);
            } catch (TelegramApiException e) {
                throw new RuntimeException();
            } catch (NoProfilesFoundException | IOException e) {
                throw new RuntimeException(e);
            }
       }

        //Check if user is currently seeking for profiles (All info provided)
        if (commandExecutor.getProfileRegistrationStage() == UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED) {

            //Checking if user is sending his latest location
            if (update.getMessage().getLocation() != null) {
                commandExecutor.setUserLatestLocation(update.getMessage().getLocation());
                userProfileRepository.save(commandExecutor);
                try {
                    //Offering new profile
                    findProfile(chatId, commandExecutor);
                } catch (IOException | TelegramApiException e)  {
                    throw new RuntimeException(e);
                }
                return;
            }

            //Checking if user is watching profiles that liked him/her
            boolean secondReview = commandExecutor.getLastInvokedCommand().equals(TelegramBotCommands.WATCH_LIKED_BY);
            boolean liked = false;

            switch (update.getMessage().getText()) {
                case ProfilesSeekingMarkupManager.CURRENT -> {
                    commandExecutor.setLastInvokedCommand("/" + ProfilesSeekingMarkupManager.CURRENT);
                    userProfileRepository.save(commandExecutor);
                    try {
                        askForLatestLocation(chatId);
                        return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ProfilesSeekingMarkupManager.DEFAULT -> {
                    commandExecutor.setProfileSeekingMode(ProfileSeekingMode.SEEKING_BY_DEFAULT_LOCATION);
                    try {
                        findProfile(chatId, commandExecutor);
                        return;
                    } catch (IOException | TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ProfilesSeekingMarkupManager.LIKED_BY -> {
                    commandExecutor.setProfileSeekingMode(ProfileSeekingMode.WATCHING_LIKED_BY_PROFILES);
                    try {
                        findLikedByProfiles(commandExecutor, chatId);
                        return;
                    } catch (IOException | TelegramApiException e) {
                        throw new RuntimeException(e);
                    } catch (NoProfilesFoundException e) {
                        try {
                            noProfilesFound(chatId);
                        } catch (TelegramApiException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                case ProfilesSeekingMarkupManager.ACCEPT_PROFILE -> {
                    try {
                        if (getAppropriateProfileWrapper(commandExecutor, chatId, secondReview, true)) return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ProfilesSeekingMarkupManager.DENY_PROFILE ->{
                    try {
                        if (getAppropriateProfileWrapper(commandExecutor, chatId, secondReview, false)) return;
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ProfilesSeekingMarkupManager.REPORT_PROFILE -> {
                    try {
                        commandExecutor.setCommandToConfirm(TelegramBotCommands.REPORT_PROFILE);
                        userProfileRepository.save(commandExecutor);
                        askConfirmCommand(chatId);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }


            }



        }









        //if user invokes preset command
        if (message[0] != null && message[0].length() > 0 && message[0].charAt(0) == '/') {
            commandExecutor.setLastInvokedCommand(message[0]);
            userProfileRepository.save(commandExecutor);

            if (message[0].equalsIgnoreCase(TelegramBotCommands.WATCH_LIKED_BY)) {
                try {
                   UserProfile userProfile =  findLikedByProfiles(commandExecutor, chatId);
                    return;
                } catch (TelegramApiException | IOException e) {
                    throw new RuntimeException(e);
                } catch (NoProfilesFoundException e) {
                    try {
                        noProfilesFound(chatId);
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                    return;
                }
            }

            if (message[0].equalsIgnoreCase(TelegramBotCommands.RESTART_PROFILE_CREATION)) {
                commandExecutor.setCommandToConfirm(TelegramBotCommands.RESTART_PROFILE_CREATION);
                userProfileRepository.save(commandExecutor);
                try {
                    askConfirmCommand(chatId);
                    return;
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            //Profile Creation
            if (message[0].equalsIgnoreCase(TelegramBotCommands.RESTART_PROFILE_CREATION) || (message[0].equalsIgnoreCase(TelegramBotCommands.START))) {
                if (commandExecutor.getProfileRegistrationStage() == UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED) return;
                SendMessage welcomeMessage = RegistrationMessageSender.sendUserRegistrationMessage(chatId, UserProfileRegistrationStage.NO_INFORMATION.getMessage(), false);
                SendMessage nameAskingMessage = RegistrationMessageSender.sendNextStepRegistrationMessage(chatId, commandExecutor.getProfileRegistrationStage(), ProfileCreationMarkupManager.getDefaultMarkup());
                try {
                    execute(welcomeMessage);
                    execute(nameAskingMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //if user fills in some details, not invoking the command
        else {
            //getting users lastly invoked command.
            String lastlyInvokedCommand = commandExecutor.getLastInvokedCommand();


            //If user creates profile
            if (lastlyInvokedCommand != null && lastlyInvokedCommand.equalsIgnoreCase(TelegramBotCommands.RESTART_PROFILE_CREATION) || lastlyInvokedCommand.equalsIgnoreCase(TelegramBotCommands.START) ) {
                //Check if user wants to restart the registration
                if (update.getMessage().getText() != null && update.getMessage().getText().equalsIgnoreCase(ProfileCreationMarkupManager.RESTART_REGISTRATION)) {
                    try {
                        commandExecutor.setCommandToConfirm(TelegramBotCommands.RESTART_PROFILE_CREATION);
                        userProfileRepository.save(commandExecutor);
                        askConfirmCommand(chatId);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                UserProfileRegistrationStage registrationStage = commandExecutor.getProfileRegistrationStage();
                if (registrationStage == UserProfileRegistrationStage.REGISTRATION_COMPLETED || registrationStage == UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED) return;
                //Checking users REGISTRATION stage.
                switch (registrationStage) {
                    case NO_INFORMATION -> {
                        if (message.length < 2) {

                        }
                        if (!ProfileRegistrationChecks.checkTheRightNameFormat(update.getMessage().getText())) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.WRONG_NAME_FORMAT));
                                return;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        commandExecutor.setFirstName(message[0]);
                        commandExecutor.setSecondName(message[1]);
                    }

                    case NAME_PROVIDED -> {
                        if (message[0] == null) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.WRONG_AGE_FORMAT));
                                return;
                            } catch (TelegramApiException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        try {
                            Date dateOfBirth = new SimpleDateFormat("dd.MM.yyyy").parse(message[0]);
                            int isValid = ProfileRegistrationChecks.checkAge(dateOfBirth);
                            commandExecutor.setDateOfBirth(dateOfBirth);

                        } catch (ParseException e) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.WRONG_AGE_FORMAT));
                                return;
                            } catch (TelegramApiException ex) {
                                throw new RuntimeException(ex);
                            }
                        }



                    }

                    case AGE_PROVIDED -> {
                        try {
                            userProfileJDBC.setUserProfileGenderAndSave(commandExecutor, update.getMessage().getText());
                        } catch (NonExistingGenderException e) {
                            try {
                                SendMessage sendMessage = ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.NO_SUCH_GENDER);
                                sendMessage.setReplyMarkup(ProfileCreationMarkupManager.getDefaultGenderSelectionMarkup());
                                execute(sendMessage);
                            } catch (TelegramApiException ex) {
                                throw new RuntimeException(ex);
                            }
                            return;
                        }
                    }
                    case GENDER_PROVIDED -> {
                        try {
                            userProfileJDBC.setSeekingForGenderAndSave(commandExecutor, update.getMessage().getText());
                        } catch (NonExistingGenderException e) {
                            try {
                                SendMessage sendMessage = ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.NO_SUCH_GENDER);
                                sendMessage.setReplyMarkup(ProfileCreationMarkupManager.getWideGenderSelectionMarkup());
                                execute(sendMessage);
                            } catch (TelegramApiException ex) {
                                throw new RuntimeException(ex);
                            }
                            return;
                        }

                    }

                    case SEEKING_FOR_GENDER_PROVIDED -> {
                        if (update.getMessage().getLocation() == null) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.NUMBERS_IN_LOCATION_NAME));
                                return;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        org.telegram.telegrambots.meta.api.objects.Location telegramGivenLocation = update.getMessage().getLocation();
                        Location defaultLocation = new Location(telegramGivenLocation.getLatitude(), telegramGivenLocation.getLongitude(), "", "", "");
                        try {
                            commandExecutor.setUserDefaultLocation(defaultLocation);
                            preciseLocationManager.setLocationCityAndRegionName(defaultLocation, commandExecutor);
                        } catch (IOException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    case DEFAULT_LOCATION_PROVIDED -> {
                        if (update.getMessage().getPhoto() == null) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.PHOTO_NOT_PROVIDED));
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        try {
                            //Getting the photos sent by user. Telegram gives us multiple pictures of different quality. Last picture has the highest resolution.
                            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
                            //downloading profile picture and temporarly saving it on the hosting device
                            File profilePic = telegramFileDownloadService.downloadFile(photoSizes.get(photoSizes.size() - 1).getFileId(), commandExecutorId);
                            String amazonProfilePictureName = profilePic.getName();
                            amazonPicturesManager.savePictureIntoCloud(profilePic, profilePic.getName() ,amazonPicturesManager.AMAZON_PICTURES_BUCKET_NAME);
                            commandExecutor.setProfilePictureLink(amazonProfilePictureName);


                        } catch (TelegramApiException | IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }


                    }

                    case PHOTO_PROVIDED -> {
                        commandExecutor.setHobbies(update.getMessage().getText());
                    }

                    case HOBBIES_PROVIDED -> {
                        commandExecutor.setAdditionalInfo(update.getMessage().getText());
                        try {
                            java.io.File profilePic = amazonPicturesManager.getProfilePicture(commandExecutor.getProfilePictureLink(), amazonPicturesManager.AMAZON_PICTURES_BUCKET_NAME);
                            execute(ProfileInfoSender.sendProfileInfo(chatId, commandExecutor, profilePic));
                            //removing unnecessary file
                            profilePic.delete();
                        } catch (TelegramApiException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
//                    case ADDITIONAL_INFO_PROVIDED, REGISTRATION_COMPLETED -> {
//
//                        try {
//                            execute(ProfileInfoSender.sendProfileInfo(chatId, commandExecutor, amazonPicturesManager));
//                        } catch (TelegramApiException e) {
//
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
                }


              //Increasing the users Registration stage by 1
                RegistrationStagesManager.increaseRegistrationStage(commandExecutor.getProfileRegistrationStage(), commandExecutor);
                //Saving changes to repo
                userProfileRepository.save(commandExecutor);
                    try {
                        ReplyKeyboardMarkup replyKeyboardMarkup =  ProfileCreationMarkupManager.determineRegistrationStageNeededMarkup(commandExecutor.getProfileRegistrationStage());
                    execute(RegistrationMessageSender.sendNextStepRegistrationMessage(chatId, commandExecutor.getProfileRegistrationStage(), replyKeyboardMarkup));
                    if (commandExecutor.getProfileRegistrationStage() == UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED)
                        startProfileSeekingMessage(chatId);
                    } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private boolean getAppropriateProfileWrapper(UserProfile commandExecutor, long chatId, boolean secondReview, boolean liked) throws TelegramApiException {
        if (secondReview) {
            reviewedAccountsManager.updateReviewedAccount(commandExecutor, userProfileRepository.getUserProfileById(commandExecutor.getCurrentReviewingProfileId()).get(), liked);
            try {
                findLikedByProfiles(commandExecutor, chatId);
            } catch (TelegramApiException | IOException e) {
                throw new RuntimeException(e);
            }
            catch (NoProfilesFoundException e) {
                noProfilesFound(chatId);
            }
            return true;
        }
        else {
            reviewedAccountsManager.createReviewedAccount(commandExecutor, liked);
            try {
                findProfile(chatId, commandExecutor);
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
        return false;
    }


    private UserProfile findProfile(long chatId, UserProfile commandExecutor) throws IOException, TelegramApiException {
        UserProfile appropriateProfile = null;
        try {
            appropriateProfile = profilesFinder.findAppropriateProfile(commandExecutor);
        } catch (Exception e) {
            noProfilesFound(chatId);
            throw new RuntimeException();
        }
        commandExecutor.setCurrentReviewingProfileId(appropriateProfile.getId());
        java.io.File file = amazonPicturesManager.getProfilePicture(appropriateProfile.getProfilePictureLink(), amazonPicturesManager.AMAZON_PICTURES_BUCKET_NAME);
        SendPhoto sendPhoto = ProfileInfoSender.sendProfileInfo(chatId, appropriateProfile, file);
        sendPhoto.setReplyMarkup(ProfilesSeekingMarkupManager.getProfileOfferMarkup());
        execute(sendPhoto);
        userProfileRepository.save(commandExecutor);
        return appropriateProfile;
    }

    public void startProfileSeekingMessage(long chatId) throws TelegramApiException {
        String message = "To start seeking for profiles, choose whether you want to see profiles at your current or your default location";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(ProfilesSeekingMarkupManager.getDefaultMarkup());
        execute(sendMessage);
    }

    public void askForLatestLocation(long chatId) throws TelegramApiException {
        String message = "Please send your latest location through telegram";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(null);
        execute(sendMessage);
    }

    public void askConfirmCommand(long chatId) throws TelegramApiException {
        execute(CommandConfirmationMessageSender.getDefaultCommandConfirmationMessage(chatId));
    }

    public void performConfirmedCommand(long chatId, UserProfile userProfile, boolean confirmed) throws TelegramApiException, NoProfilesFoundException, IOException {
        String command = userProfile.getCommandToConfirm();
        if (!confirmed){
            userProfile.setCommandToConfirm(null);
            userProfile.setLastInvokedCommand(TelegramBotCommands.START);
            if (userProfile.getProfileSeekingMode() == ProfileSeekingMode.WATCHING_LIKED_BY_PROFILES) {
                findLikedByProfiles(userProfile, chatId);
            }
            else {
                findProfile(chatId, userProfile);
            }
        }

        else {
            switch (command) {
                case TelegramBotCommands.RESTART_PROFILE_CREATION -> {
                    fullResetUser(userProfile);
                    execute(RegistrationMessageSender.sendUserRegistrationMessage(chatId, UserProfileRegistrationStage.NAME_PROVIDED.getMessage(), true));
                }
                case TelegramBotCommands.REPORT_PROFILE -> {
                    warningsManager.createNewWarning(userProfile, userProfileRepository.getUserProfileById(userProfile.getCurrentReviewingProfileId()).get());
                }
            }
        }
        userProfileRepository.save(userProfile);
    }



    private void fullResetUser(UserProfile userProfile){
        if (userProfile.getProfilePictureLink() != null) amazonPicturesManager.deleteProfilePicture(userProfile.getProfilePictureLink(), amazonPicturesManager.AMAZON_PICTURES_BUCKET_NAME);
        userProfile.resetProfile();
        userProfile.setCommandToConfirm(null);
        userProfileRepository.save(userProfile);
    }

    private UserProfile findLikedByProfiles(UserProfile commandExecutor, long chatId) throws NoProfilesFoundException, IOException, TelegramApiException {
        commandExecutor.setProfileSeekingMode(ProfileSeekingMode.WATCHING_LIKED_BY_PROFILES);
        UserProfile appropriateProfile = profilesFinder.findLikedByProfiles(commandExecutor);
        commandExecutor.setCurrentReviewingProfileId(appropriateProfile.getId());
        userProfileRepository.save(commandExecutor);
        SendPhoto sendPhoto= ProfileInfoSender.sendProfileInfo(chatId, appropriateProfile, amazonPicturesManager.getProfilePicture(appropriateProfile.getProfilePictureLink(), AmazonPicturesManager.AMAZON_PICTURES_BUCKET_NAME));
        sendPhoto.setReplyMarkup(ProfilesSeekingMarkupManager.getProfileOfferMarkup());
        execute(sendPhoto);
        return appropriateProfile;
    }

    private void noProfilesFound(long chatId) throws TelegramApiException {
        SendMessage sendMessage = ErrorMessageSender.sendNewErrorMessage(chatId,ErrorMessageSender.NO_APPROPRIATE_PROFILES_FOUND);
        sendMessage.setReplyMarkup(ProfilesSeekingMarkupManager.getDefaultMarkup());
        execute(sendMessage);
    }










}

package com.example.TelegramBot.TelegramManaging;

import com.example.TelegramBot.AmazonServices.AmazonPicturesManager;
import com.example.TelegramBot.Domains.Location;
import com.example.TelegramBot.Domains.ProfileSeekingMode;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.LocationServices.PreciseLocationManager;
import com.example.TelegramBot.MessageSenders.ErrorMessageSender;
import com.example.TelegramBot.MessageSenders.ProfileInfoSender;
import com.example.TelegramBot.MessageSenders.RegistrationMessageSender;
import com.example.TelegramBot.ProfilesSeeking.ProfilesFinder;
import com.example.TelegramBot.RegistrationStagesManager.RegistrationStagesManager;
import com.example.TelegramBot.ReplyMarkupManager.ProfileCreationMarkupManager;
import com.example.TelegramBot.ReplyMarkupManager.ProfilesSeekingMarkupManager;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
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

    private final Clock clock;

    @Autowired
    public TelegramBot(UserProfileRepository userProfileRepository, UserProfileJDBC userProfileJDBC, AmazonPicturesManager amazonPicturesManager, PreciseLocationManager preciseLocationManager, ProfilesFinder profilesFinder, Clock clock) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileJDBC = userProfileJDBC;
        this.amazonPicturesManager = amazonPicturesManager;
        this.preciseLocationManager = preciseLocationManager;
        this.profilesFinder = profilesFinder;
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
        if (!userProfileJDBC.checkIfUserProfileExists(update.getMessage().getChatId())) {
            userProfileJDBC.createNewUserProfile(update.getMessage().getChatId());
        }

        //Getting the new user
        UserProfile commandExecutor = userProfileRepository.getUserProfileById(update.getMessage().getChatId()).get();

        //The new command executor id and the chat id. They are now the same, seperated for the code clearance
        long commandExecutorId = commandExecutor.getId();
        long chatId = commandExecutorId;




        if (commandExecutor.getProfileRegistrationStage() == UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED) {

            //Checking if user is sending his latest location
            if (update.getMessage().getLocation() != null) {
                commandExecutor.setUserLatestLocation(update.getMessage().getLocation());
                userProfileRepository.save(commandExecutor);
                findProfile(chatId);
                return;
            }

            switch (message[0]) {
                case ProfilesSeekingMarkupManager.CURRENT -> {
                    commandExecutor.setLastInvokedCommand("/" + ProfilesSeekingMarkupManager.CURRENT);
                    try {
                        askForLatestLocation(chatId);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                case ProfilesSeekingMarkupManager.DEFAULT -> {
                    commandExecutor.setProfileSeekingMode(ProfileSeekingMode.SEEKING_BY_DEFAULT_LOCATION);
                    break;
                }
            }
        }





        //if user invokes preset command
        if (message[0] != null && message[0].length() > 0 && message[0].charAt(0) == '/') {
            commandExecutor.setLastInvokedCommand(message[0]);
            userProfileRepository.save(commandExecutor);
            //Profile Creation
            if (message[0].equalsIgnoreCase("/createprofile")) {
                if (commandExecutor.getProfileRegistrationStage() == UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED) return;
                SendMessage welcomeMessage = RegistrationMessageSender.sendUserRegistrationMessage(chatId, UserProfileRegistrationStage.NO_INFORMATION.getMessage(), true);
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
            if (lastlyInvokedCommand.equalsIgnoreCase("/createprofile")) {
                //Check if user wants to restart the registration
                if (update.getMessage().getText() != null && update.getMessage().getText().equalsIgnoreCase(ProfileCreationMarkupManager.RESTART_REGISTRATION)) {
                    //Deleting old picture if it exists from S3 bucket
                    if (commandExecutor.getProfilePictureLink() != null) amazonPicturesManager.deleteProfilePicture(commandExecutor.getProfilePictureLink(), amazonPicturesManager.AMAZON_PICTURES_BUCKET_NAME);
                    //Reseting Profile
                    commandExecutor.resetProfile();
                    userProfileRepository.save(commandExecutor);
                    SendMessage welcomeMessage = RegistrationMessageSender.sendUserRegistrationMessage(chatId, UserProfileRegistrationStage.NAME_PROVIDED.getMessage(), true);
                    try {
                        execute(welcomeMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                UserProfileRegistrationStage registrationStage = commandExecutor.getProfileRegistrationStage();
                if (registrationStage == UserProfileRegistrationStage.REGISTRATION_COMPLETED) return;
                //Checking users REGISTRATION stage.
                switch (registrationStage) {
                    case NO_INFORMATION -> {
                        if (message.length < 2) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.WRONG_NAME_FORMAT));
                                break;
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
                                break;
                            } catch (TelegramApiException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        try {
                            Date dateOfBirth = new SimpleDateFormat("dd.MM.yyyy").parse(message[0]);
                            LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateOfBirth.getTime()), ZoneId.systemDefault());

                            if (time.getYear() < 1940) {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.TOO_HIGH_AGE));
                            }
                            else if (time.isAfter(LocalDateTime.now())) {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.FUTURE_DATE));
                            }
                            commandExecutor.setDateOfBirth(dateOfBirth);

                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.WRONG_AGE_FORMAT));
                            } catch (TelegramApiException ex) {
                                throw new RuntimeException(ex);
                            }
                        }



                    }

                    case AGE_PROVIDED -> {
                        if (update.getMessage().getLocation() == null) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.WRONG_NAME_FORMAT));
                                break;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        org.telegram.telegrambots.meta.api.objects.Location telegramGivenLocation = update.getMessage().getLocation();
                        Location defaultLocation = new Location(telegramGivenLocation.getLatitude(), telegramGivenLocation.getLongitude(), "", "", "");
                        try {
                            preciseLocationManager.setLocationCityAndRegionName(defaultLocation);
                        } catch (IOException | URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        commandExecutor.setUserDefaultLocation(defaultLocation);
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
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
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
                        ReplyKeyboardMarkup replyKeyboardMarkup =  ProfileCreationMarkupManager.getDefaultMarkup();
                    execute(RegistrationMessageSender.sendNextStepRegistrationMessage(chatId, commandExecutor.getProfileRegistrationStage(), replyKeyboardMarkup));
                    if (commandExecutor.getProfileRegistrationStage() == UserProfileRegistrationStage.ADDITIONAL_INFO_PROVIDED)
                        startProfileSeekingMessage(chatId);
                    } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private void findProfile(long chatId) {

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


}

package com.example.TelegramBot.TelegramManaging;

import com.example.TelegramBot.AmazonServices.AmazonPicturesManager;
import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.MessageSenders.ErrorMessageSender;
import com.example.TelegramBot.MessageSenders.ProfileInfoSender;
import com.example.TelegramBot.MessageSenders.RegistrationMessageSender;
import com.example.TelegramBot.RegistrationStagesManager.RegistrationStagesManager;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Autowired
    public TelegramBot(UserProfileRepository userProfileRepository, UserProfileJDBC userProfileJDBC, AmazonPicturesManager amazonPicturesManager) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileJDBC = userProfileJDBC;
        this.amazonPicturesManager = amazonPicturesManager;
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

        if (!userProfileJDBC.checkIfUserProfileExists(update.getMessage().getChatId())) {
            userProfileJDBC.createNewUserProfile(update.getMessage().getChatId());
        }
        UserProfile commandExecutor = userProfileRepository.getUserProfileById(update.getMessage().getChatId()).get();

        long commandExecutorId = commandExecutor.getId();
        long chatId = commandExecutorId;

        //if user invokes preset command
        if (message[0] != null && message[0].length() > 0 && message[0].charAt(0) == '/') {
            commandExecutor.setLastInvokedCommand(message[0]);
            userProfileRepository.save(commandExecutor);
            //Profile Creation
            if (message[0].equalsIgnoreCase("/createprofile")) {
                SendMessage welcomeMessage = RegistrationMessageSender.sendUserRegistrationMessage(chatId, UserProfileRegistrationStage.NO_INFORMATION.getMessage());
                SendMessage nameAskingMessage = RegistrationMessageSender.sendNextStepRegistrationMessage(chatId, commandExecutor.getProfileRegistrationStage());
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
            String lastlyInvokedCommand = commandExecutor.getLastInvokedCommand();

            if (lastlyInvokedCommand.equalsIgnoreCase("/createprofile")) {
                UserProfileRegistrationStage registrationStage = commandExecutor.getProfileRegistrationStage();
                if (registrationStage == UserProfileRegistrationStage.REGISTRATION_COMPLETED) return;
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
                                execute(ErrorMessageSender.sendNewErrorMessage(commandExecutorId, ErrorMessageSender.TOO_HIGH_AGE));
                            }
                            commandExecutor.setDateOfBirth(dateOfBirth);

                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }



                    }

                    case AGE_PROVIDED -> {
                        if (message[0] == null || !message[0].matches("[a-z]+")) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(chatId, ErrorMessageSender.WRONG_NAME_FORMAT));
                                break;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        commandExecutor.setUserDefaultLocation(message[0]);
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
                            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
                            File profilePic = telegramFileDownloadService.downloadFile(photoSizes.get(photoSizes.size() - 1).getFileId(), commandExecutorId);

                            String profilePictureLink
                            amazonPicturesManager.savePictureIntoCloud(profilePic, profilePic.getName());
                            commandExecutor.setProfilePictureLink(profilePic.ge);

                        } catch (TelegramApiException | IOException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }


                    }

                    case PHOTO_PROVIDED -> {
                        commandExecutor.setHobbies(update.getMessage().getText());
                    }

                    case HOBBIES_PROVIDED -> {
                        commandExecutor.setAdditionalInfo(update.getMessage().getText());
                    }
                    case REGISTRATION_COMPLETED -> {
                        try {
                            execute(ProfileInfoSender.sendProfileInfo(chatId, commandExecutor));
                        } catch (TelegramApiException e) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
                        }
                    }

                }
                RegistrationStagesManager.increaseRegistrationStage(commandExecutor.getProfileRegistrationStage(), commandExecutor);
                userProfileRepository.save(commandExecutor);
                try {
                    execute(RegistrationMessageSender.sendNextStepRegistrationMessage(chatId, commandExecutor.getProfileRegistrationStage()));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

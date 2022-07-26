package com.example.TelegramBot.TelegramManaging;

import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.JDBC.UserProfileJDBC;
import com.example.TelegramBot.MessageSenders.ErrorMessageSender;
import com.example.TelegramBot.MessageSenders.RegistrationMessageSender;
import com.example.TelegramBot.RegistrationStagesManager.RegistrationStagesManager;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
public class TelegramBot extends TelegramLongPollingBot {


    private final UserProfileRepository userProfileRepository;

    private final UserProfileJDBC userProfileJDBC;

    @Autowired
    public TelegramBot(UserProfileRepository userProfileRepository, UserProfileJDBC userProfileJDBC) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileJDBC = userProfileJDBC;
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

        if (update.getMessage().getText() != null) message = update.getMessage().getText().split("\\s+");
        else message = new String[1];

        if (!userProfileJDBC.checkIfUserProfileExists(update.getMessage().getChatId())) {
            userProfileJDBC.createNewUserProfile(update.getMessage().getChatId());
        }
        UserProfile commandExecutor = userProfileRepository.getUserProfileById(update.getMessage().getChatId()).get();

        long commandExecutorId = commandExecutor.getId();

        //if user invokes preset command
        if (message[0] != null && message[0].length() > 0 && message[0].charAt(0) == '/') {
            commandExecutor.setLastInvokedCommand(message[0]);
            userProfileRepository.save(commandExecutor);
            //Profile Creation
            if (message[0].equalsIgnoreCase("/createprofile")) {
                SendMessage welcomeMessage = RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId,RegistrationMessageSender.WELCOME_MESSAGE);
                SendMessage nameAskingMessage = RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId,RegistrationMessageSender.PROVIDE_FULL_NAME);
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
                                execute(ErrorMessageSender.sendNewErrorMessage(commandExecutorId, ErrorMessageSender.WRONG_NAME_FORMAT));
                                break;
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        commandExecutor.setFirstName(message[0]);
                        commandExecutor.setSecondName(message[1]);
                        commandExecutor.setProfileRegistrationStage(UserProfileRegistrationStage.NAME_PROVIDED);
                        RegistrationStagesManager.increaseRegistrationStage(commandExecutor.getProfileRegistrationStage(), commandExecutor);
                        userProfileRepository.save(commandExecutor);
                        break;
                    }

                    case NAME_PROVIDED -> {
                        if (message[0] == null) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(commandExecutorId, ErrorMessageSender.WRONG_AGE_FORMAT));
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
                            commandExecutor.setProfileRegistrationStage(UserProfileRegistrationStage.AGE_PROVIDED);
                        } catch (ParseException e) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(commandExecutorId, ErrorMessageSender.WRONG_AGE_FORMAT));
                            } catch (TelegramApiException ex) {
                                throw new RuntimeException(ex);
                            }
                            ;

                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            execute(RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId, RegistrationMessageSender.PROVIDE_AGE));
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        RegistrationStagesManager.increaseRegistrationStage(commandExecutor.getProfileRegistrationStage(), commandExecutor);
                        userProfileRepository.save(commandExecutor);

                        break;


                    }

                    case AGE_PROVIDED -> {
                        if (message[0] == null || !message[0].matches("[a-z]+")) {
                            try {
                                execute(ErrorMessageSender.sendNewErrorMessage(commandExecutorId, ErrorMessageSender.WRONG_NAME_FORMAT));
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        commandExecutor.setUserDefaultLocation(message[0]);
                        commandExecutor.setProfileRegistrationStage(UserProfileRegistrationStage.DEFAULT_LOCATION_PROVIDED);
                        try {
                            execute(RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId, RegistrationMessageSender.PROVIDE_DEFAULT_LOCATION));
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        RegistrationStagesManager.increaseRegistrationStage(commandExecutor.getProfileRegistrationStage(), commandExecutor);
                        userProfileRepository.save(commandExecutor);
                        break;
                    }

                    case DEFAULT_LOCATION_PROVIDED -> {
                        try {
                            execute(RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId, RegistrationMessageSender.PROVIDE_PHOTO));
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }

                    case PHOTO_PROVIDED -> {
                        try {
                            execute(RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId, RegistrationMessageSender.PROVIDE_HOBBIES));
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    case HOBBIES_PROVIDED -> {
                        commandExecutor.setHobbies(update.getMessage().getText());
                        try {
                            execute(RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId, RegistrationMessageSender.PROVIDE_ADDITIONAL_INFO));
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        RegistrationStagesManager.increaseRegistrationStage(commandExecutor.getProfileRegistrationStage(), commandExecutor);
                        userProfileRepository.save(commandExecutor);
                    }

                    case ADDITIONAL_INFO_PROVIDED -> {
                        commandExecutor.setHobbies(update.getMessage().getText());
                        try {
                            execute(RegistrationMessageSender.sendUserRegistrationMessage(commandExecutorId, RegistrationMessageSender.REGISTRATION_COMPLETED));
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        RegistrationStagesManager.increaseRegistrationStage(commandExecutor.getProfileRegistrationStage(), commandExecutor);
                        userProfileRepository.save(commandExecutor);

                    }




                }
            }
        }
    }
}

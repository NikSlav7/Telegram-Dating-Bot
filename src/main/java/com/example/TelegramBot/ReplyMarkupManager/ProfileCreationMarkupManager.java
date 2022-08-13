package com.example.TelegramBot.ReplyMarkupManager;


import com.example.TelegramBot.Domains.Gender;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import com.example.TelegramBot.Interfaces.ReplyKeyboardMarkupManager;
import com.example.TelegramBot.ProfileRegistration.ProfileRegistrationChecks;
import org.aspectj.weaver.tools.cache.CacheBacking;
import org.checkerframework.checker.units.qual.K;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileCreationMarkupManager implements ReplyKeyboardMarkupManager {

    public static final String CANCEL = "Cancel ❌";

    public static final String RESTART_REGISTRATION = "Restart \uD83D\uDD19";

    public static final String SEND_LOCATION = "Send your location \uD83D\uDC4C";


    public static ReplyKeyboardMarkup getDefaultMarkup() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton restartButton = new KeyboardButton(RESTART_REGISTRATION);
        //adding buttons to rows
        KeyboardRow first = new KeyboardRow();
        first.add(restartButton);

        //adding rows to keyboard
        List<KeyboardRow> buttonsList = new ArrayList<>();
        buttonsList.add(first);

        markup.setResizeKeyboard(true);
        markup.setKeyboard(buttonsList);

        return markup;
    }

    public static ReplyKeyboardMarkup getLocationAskingDefaultMarkup() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton sendLocation = new KeyboardButton(SEND_LOCATION);
        sendLocation.setRequestLocation(true);

        KeyboardRow first = new KeyboardRow(List.of(sendLocation));

        markup.setKeyboard(new ArrayList<>(List.of(first)));

        return markup;


    }

    public static ReplyKeyboardMarkup getDefaultGenderSelectionMarkup(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton male = new KeyboardButton(Gender.MALE.getMessage());
        KeyboardButton female = new KeyboardButton(Gender.FEMALE.getMessage());

        KeyboardRow row = new KeyboardRow(List.of(male, female));

        markup.setKeyboard(List.of(row));
        markup.setResizeKeyboard(true);

        return markup;
    }

    public static ReplyKeyboardMarkup getWideGenderSelectionMarkup(){
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton male = new KeyboardButton(Gender.MALE.getMessage());
        KeyboardButton female = new KeyboardButton(Gender.FEMALE.getMessage());
        KeyboardButton third = new KeyboardButton(Gender.BOTH.getMessage());

        KeyboardRow row = new KeyboardRow(List.of(male, female));
        KeyboardRow row1 = new KeyboardRow(List.of(third));

        markup.setKeyboard(List.of(row, row1));
        markup.setResizeKeyboard(true);

        return markup;
    }
    public static ReplyKeyboardMarkup determineRegistrationStageNeededMarkup(UserProfileRegistrationStage profileRegistrationStage){
        if (profileRegistrationStage == UserProfileRegistrationStage.AGE_PROVIDED){
            return getDefaultGenderSelectionMarkup();
        }
        if (profileRegistrationStage == UserProfileRegistrationStage.GENDER_PROVIDED) {
            return getWideGenderSelectionMarkup();
        }
        return getDefaultMarkup();
    }

}

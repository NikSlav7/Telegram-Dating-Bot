package com.example.TelegramBot.ReplyMarkupManager;

import com.example.TelegramBot.Interfaces.ReplyKeyboardMarkupManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;


@Component
public class ProfilesSeekingMarkupManager implements ReplyKeyboardMarkupManager {

    public static final String CURRENT = "Current";
    public static final String DEFAULT = "Default";
    public static final String ACCEPT_PROFILE = "Like";
    public static final String DENY_PROFILE = "Don't like";


    public static ReplyKeyboardMarkup getDefaultMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow row = new KeyboardRow(getDefaultButtonsList());
        replyKeyboardMarkup.setKeyboard(List.of(row));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getProfileOfferMarkup(){

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardRow accept = new KeyboardRow(List.of(getProfileOfferButtonsList().get(0)));
        KeyboardRow deny = new KeyboardRow(List.of(getProfileOfferButtonsList().get(1)));
        KeyboardRow modeChoose = new KeyboardRow(getDefaultButtonsList());
        replyKeyboardMarkup.setKeyboard(List.of(accept, deny, modeChoose));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }


    private static List<KeyboardButton> getDefaultButtonsList(){
        KeyboardButton currentLoc = new KeyboardButton(CURRENT);
        KeyboardButton defaultLoc = new KeyboardButton(DEFAULT);
        return List.of(currentLoc, defaultLoc);
    }

    private static List<KeyboardButton> getProfileOfferButtonsList(){
        KeyboardButton accept = new KeyboardButton(ACCEPT_PROFILE);
        KeyboardButton deny = new KeyboardButton(DENY_PROFILE);
        return List.of(accept, deny);
    }



}

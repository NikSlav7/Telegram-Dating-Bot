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

    public static ReplyKeyboardMarkup getDefaultMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        KeyboardButton currentLoc = new KeyboardButton(CURRENT);
        KeyboardButton defaultLoc = new KeyboardButton(DEFAULT);
        KeyboardRow row = new KeyboardRow(List.of(currentLoc, defaultLoc));
        replyKeyboardMarkup.setKeyboard(List.of(row));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

}

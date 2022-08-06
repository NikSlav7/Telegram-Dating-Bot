package com.example.TelegramBot.ReplyMarkupManager;


import com.example.TelegramBot.Interfaces.ReplyKeyboardMarkupManager;
import org.aspectj.weaver.tools.cache.CacheBacking;
import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

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

}

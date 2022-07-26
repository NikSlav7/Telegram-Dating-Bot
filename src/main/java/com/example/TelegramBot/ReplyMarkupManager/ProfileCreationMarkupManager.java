package com.example.TelegramBot.ReplyMarkupManager;


import org.jvnet.hk2.annotations.Service;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProfileCreationMarkupManager {


    public static ReplyKeyboardMarkup getProfileCreationDefaultMarkup() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton cancelButton = new KeyboardButton("Cancel ❌");
        KeyboardButton restartButton = new KeyboardButton("Restart \uD83D\uDD19");
        //adding buttons to rows
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(cancelButton);
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(restartButton);

        //adding rows to keyboard
        List<KeyboardRow> buttonsList = new ArrayList<>();
        buttonsList.add(firstRow);
        buttonsList.add(secondRow);

        markup.setKeyboard(buttonsList);

        return markup;
    }

}

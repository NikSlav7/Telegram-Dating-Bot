package com.example.TelegramBot.ReplyMarkupManager;

import com.example.TelegramBot.Interfaces.ReplyKeyboardMarkupManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.swing.text.StyleContext;
import java.util.List;


@Component
public class CommandConfirmationMarkupManager implements ReplyKeyboardMarkupManager {

    public static final String COMMAND_CONFIRM = "Yes, sure";

    public static final String COMMAND_CANCEL = "No, cancel";


    public static  ReplyKeyboardMarkup getDefaultMarkup() {
       ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow confirmation = new KeyboardRow(getGetCommandConfirmationButtonsList());
        markup.setKeyboard(List.of(confirmation));
        return markup;
    }

    private static List<KeyboardButton> getGetCommandConfirmationButtonsList(){
        KeyboardButton confirm = new KeyboardButton(COMMAND_CONFIRM);
        KeyboardButton cancel = new KeyboardButton(COMMAND_CANCEL);
        return List.of(confirm, cancel);
    }
}

package com.example.TelegramBot.MessageSenders;


import com.example.TelegramBot.ReplyMarkupManager.CommandConfirmationMarkupManager;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class CommandConfirmationMessageSender {

    public static final String ASK_FOR_COMMAND_CONFIRMATION = "Are you sure you want to perform this action?";

    public static SendMessage getDefaultCommandConfirmationMessage(long chatId){
        SendMessage message = new SendMessage();
        message.setText(ASK_FOR_COMMAND_CONFIRMATION);
        message.setChatId(chatId);
        message.setReplyMarkup(CommandConfirmationMarkupManager.getDefaultMarkup());
        return message;
    }
}

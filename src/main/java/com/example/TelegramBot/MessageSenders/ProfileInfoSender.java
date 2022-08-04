package com.example.TelegramBot.MessageSenders;


import com.example.TelegramBot.Domains.UserProfile;
import org.checkerframework.checker.units.qual.A;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ProfileInfoSender {




    public static SendMessage sendProfileInfo(long sendChatId, UserProfile userProfile){
        SendMessage sendMessage = new SendMessage();
        StringBuilder messageBuilder = new StringBuilder("Your profile review");
        messageBuilder.append("\n" + userProfile.getFirstName() + " " + userProfile.getSecondName());

        messageBuilder.append("\n" + java.util.Date.from(Instant.ofEpochSecond(java.util.Date.from(Instant.now()).getTime() - userProfile.getDateOfBirth().getTime())) + " y.o");

        sendMessage.setText(messageBuilder.toString());
        return sendMessage;
    }
}

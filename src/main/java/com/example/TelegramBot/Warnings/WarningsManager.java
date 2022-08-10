package com.example.TelegramBot.Warnings;


import com.example.TelegramBot.Domains.UserProfile;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.UUID;

@Component
public class WarningsManager {

    public void createNewWarning(UserProfile issuedBy, UserProfile issuedTo) {
        UserWarning userWarning = new UserWarning();
        userWarning.setId(UUID.randomUUID().toString());
        userWarning.setCreationDate(new Date(System.currentTimeMillis()));
    }
}

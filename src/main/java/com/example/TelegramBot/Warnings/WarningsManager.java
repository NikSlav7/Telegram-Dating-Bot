package com.example.TelegramBot.Warnings;


import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Repositories.UserWarningsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.UUID;

@Component
public class WarningsManager {


    private final UserWarningsRepo userWarningsRepo;


    @Autowired
    public WarningsManager(UserWarningsRepo userWarningsRepo) {
        this.userWarningsRepo = userWarningsRepo;
    }

    public UserWarning createNewWarning(UserProfile issuedBy, UserProfile issuedTo) {
        UserWarning userWarning = new UserWarning();
        userWarning.setId(UUID.randomUUID().toString());
        userWarning.setCreationDate(new Date(System.currentTimeMillis()));
        userWarning.setWarningTo(issuedTo);
        userWarning.setWarningFrom(issuedBy);
        return userWarning;
    }
}

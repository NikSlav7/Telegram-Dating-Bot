package com.example.TelegramBot.TelegramManaging;


import com.amazonaws.services.dynamodbv2.xspec.S;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TelegramBotCommands {

    public static final String RESTART_PROFILE_CREATION = "/resetprofile";

    public static final String START = "/start";

    public static final String WATCH_LIKED_BY = "/watchliked";
    Map<String, String> commandsAndDefinitions;


    public Map<String, String> getBotCommands() {
        if (commandsAndDefinitions == null) {
            initializeCommandsAndDefinitions();
        }
        return commandsAndDefinitions;
    }

    private void initializeCommandsAndDefinitions() {
        commandsAndDefinitions = new HashMap<>();
        commandsAndDefinitions.put(START, "Starts the bot");
        commandsAndDefinitions.put(RESTART_PROFILE_CREATION, "Restarts you profile (Clears all the data about it)");
        commandsAndDefinitions.put(WATCH_LIKED_BY, "See all profiles that have liked you");
    }
}

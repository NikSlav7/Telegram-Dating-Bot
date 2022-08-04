package com.example.TelegramBot.RegistrationStagesManager;


import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Domains.UserProfileRegistrationStage;
import org.jvnet.hk2.annotations.Service;

@Service
public class RegistrationStagesManager {

    public static void increaseRegistrationStage(UserProfileRegistrationStage registrationStage, UserProfile userProfile){
        UserProfileRegistrationStage[] stages = UserProfileRegistrationStage.values();
        for (int i = 0; i < stages.length; i++) {
            if (stages[i].equals(registrationStage) && i != stages.length - 1) userProfile.setProfileRegistrationStage(stages[i+1]);
        }
    }
}

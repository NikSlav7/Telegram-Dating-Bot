package com.example.TelegramBot.ProfileRegistration;


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

    public static boolean hasNeededRegistrationStage(UserProfileRegistrationStage current, UserProfileRegistrationStage needed) {
        UserProfileRegistrationStage[] stages = UserProfileRegistrationStage.values();
        for (int i = 0;  i < stages.length; i++) {
            if (stages[i] == current) return true;
            else if (stages[i] == needed) return false;
        }
        return true;
    }
}

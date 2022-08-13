package com.example.TelegramBot.ProfileRegistration;


import com.example.TelegramBot.Time.TimesManager;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProfileRegistrationChecks {

    public final static int FUTURE_DATE = 1;
    public final static int TOO_OLD = 2;
    public final static int TOO_YOUNG = 3;


    public static boolean checkTheRightNameFormat(String name){
        String[] nameArray = name.split("\\s+");
        return nameArray.length >= 2 && checkStringContainsOnlyLetters(name);
    }

    public static boolean checkStringContainsOnlyLetters(String s){
        for (Character c : s.toCharArray()) {
            if (c == ' ') continue;
            if(!Character.isLetter(c)) return false;
        }
        return true;
    }

    public static int checkAge(java.util.Date date) {
        if (date.after(new Date(System.currentTimeMillis()))) return FUTURE_DATE;
        if (TimesManager.millisToYears(System.currentTimeMillis() - date.getTime()) > 120) return TOO_OLD;
        if (TimesManager.millisToYears(System.currentTimeMillis() - date.getTime()) < 5) return TOO_YOUNG;
        return 0;
    }

}

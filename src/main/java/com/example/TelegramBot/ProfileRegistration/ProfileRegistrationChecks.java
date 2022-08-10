package com.example.TelegramBot.ProfileRegistration;


import org.springframework.stereotype.Component;

@Component
public class ProfileRegistrationChecks {



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

}

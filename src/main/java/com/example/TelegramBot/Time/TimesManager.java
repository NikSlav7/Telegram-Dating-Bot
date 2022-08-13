package com.example.TelegramBot.Time;


import org.springframework.stereotype.Component;


public class TimesManager {


    public static long getDaysInMillis(int days) {
        return (long) days * 24 * 60 * 60 * 1000;
    }

    public static long millisToYears(long millis) {return  millis / 1000 / 60 / 60 / 24 / 365;}

}

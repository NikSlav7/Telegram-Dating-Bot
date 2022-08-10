package com.example.TelegramBot.TimeCOnfigur;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ClockConfiguration {


    @Bean
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }
}

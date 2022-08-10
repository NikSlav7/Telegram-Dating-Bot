package com.example.TelegramBot.ProfilesSeeking;


import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Date;

@Component
@EnableScheduling
public class OldReviewedAccountsManager {


    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public OldReviewedAccountsManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    @Scheduled(fixedDelay = 1000)
    private void deleteOldViewedProfiles() {
        System.out.println("schedule");
        //deleting old viewed_profiles. Old ones are those which were reviewed day or longer ago.
        jdbcTemplate.update("DELETE FROM watched_profile WHERE DATE_PART('minute', AGE(current_date, profile_review_time)) > 1");
    }
}

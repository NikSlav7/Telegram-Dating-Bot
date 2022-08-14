package com.example.TelegramBot.JDBC;


import com.example.TelegramBot.Repositories.BannedProfileRepository;
import com.example.TelegramBot.Repositories.UserProfileRepository;
import com.example.TelegramBot.Warnings.BannedProfile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BannedProfileJDBC {


    private final JdbcTemplate jdbcTemplate;

    private final BannedProfileRepository bannedProfileRepository;

    private final UserProfileRepository userProfileRepository;

    public BannedProfileJDBC(JdbcTemplate jdbcTemplate, BannedProfileRepository bannedProfileRepository, UserProfileRepository userProfileRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bannedProfileRepository = bannedProfileRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public BannedProfile createNewBannedProfile(long id) {
        BannedProfile bannedProfile = new BannedProfile(id);
        return bannedProfileRepository.save(bannedProfile);
    }





}

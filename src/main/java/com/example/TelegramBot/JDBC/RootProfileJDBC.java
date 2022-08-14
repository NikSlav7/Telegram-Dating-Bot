package com.example.TelegramBot.JDBC;


import com.example.TelegramBot.Domains.RootProfile;
import com.example.TelegramBot.Repositories.RootProfileRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RootProfileJDBC {


    private final JdbcTemplate jdbcTemplate;

    private final RootProfileRepository rootProfileRepository;

    public RootProfileJDBC(JdbcTemplate jdbcTemplate, RootProfileRepository rootProfileRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.rootProfileRepository = rootProfileRepository;
    }

    public RootProfile createNewRootProfile(long id){
        RootProfile rootProfile = new RootProfile();
        rootProfile.setId(id);
        return rootProfileRepository.save(rootProfile);
    }
}

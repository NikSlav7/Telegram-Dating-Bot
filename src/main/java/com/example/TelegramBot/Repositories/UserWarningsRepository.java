package com.example.TelegramBot.Repositories;


import com.example.TelegramBot.Domains.UserProfile;
import com.example.TelegramBot.Warnings.UserWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserWarningsRepository extends JpaRepository<UserWarning, String> {

    Optional<UserWarning> getUserWarningByWarningFromAndWarningTo(UserProfile warningFrom, UserProfile warningTo);
}

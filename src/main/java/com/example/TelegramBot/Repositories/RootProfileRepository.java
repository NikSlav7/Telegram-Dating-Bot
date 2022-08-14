package com.example.TelegramBot.Repositories;


import com.example.TelegramBot.Domains.RootProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RootProfileRepository  extends JpaRepository<RootProfile, Long> {
    Optional<RootProfile> getRootProfileById(long id);
}

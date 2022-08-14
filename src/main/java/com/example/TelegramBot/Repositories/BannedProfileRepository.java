package com.example.TelegramBot.Repositories;


import com.example.TelegramBot.Warnings.BannedProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BannedProfileRepository extends JpaRepository<BannedProfile, Long> {
    Optional<BannedProfile> getBannedProfileById(long id);
}

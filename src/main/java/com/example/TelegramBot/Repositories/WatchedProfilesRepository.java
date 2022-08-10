package com.example.TelegramBot.Repositories;

import com.example.TelegramBot.Domains.WatchedProfile;
import com.example.TelegramBot.IDs.WatchedProfileId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchedProfilesRepository extends JpaRepository<WatchedProfile, WatchedProfileId> {
    Optional<WatchedProfile> getWatchedProfileByWatchedProfileId(WatchedProfileId watchedProfileId);
}

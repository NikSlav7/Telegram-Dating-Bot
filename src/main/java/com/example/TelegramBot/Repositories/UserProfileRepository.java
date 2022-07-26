package com.example.TelegramBot.Repositories;

import com.example.TelegramBot.Domains.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> getUserProfileById(long id);
}

package com.example.TelegramBot.Repositories;


import com.example.TelegramBot.Warnings.UserWarning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWarningsRepo extends JpaRepository<UserWarning, String> {
}

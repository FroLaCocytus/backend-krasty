package com.example.server.repository;

import com.example.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<UserEntity, Integer> {
    UserEntity findByLogin(String login);

}

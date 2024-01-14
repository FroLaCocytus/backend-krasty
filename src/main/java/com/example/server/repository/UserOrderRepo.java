package com.example.server.repository;

import com.example.server.entity.UserEntity;
import com.example.server.entity.UserOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserOrderRepo extends JpaRepository<UserOrderEntity, Integer> {

    List<UserOrderEntity> findByUserId(UserEntity user);
}

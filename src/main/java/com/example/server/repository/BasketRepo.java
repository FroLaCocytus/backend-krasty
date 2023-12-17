package com.example.server.repository;

import com.example.server.entity.BasketEntity;
import com.example.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepo extends JpaRepository<BasketEntity, Integer> {

    BasketEntity findByUserId(UserEntity userId);

}

package com.example.server.repository;

import com.example.server.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<RoleEntity, Integer> {
    RoleEntity findByName(String role);

}
package com.example.server.repository;

import com.example.server.entity.InitializationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InitializationStatusRepo extends JpaRepository<InitializationStatus, Integer> {
}

package com.example.server.repository;

import com.example.server.entity.MerchandiseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MerchandiseRepo extends JpaRepository<MerchandiseEntity, Integer> {
    MerchandiseEntity findByTitle(String title);
}

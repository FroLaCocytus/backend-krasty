package com.example.server.repository;

import com.example.server.entity.BasketEntity;
import com.example.server.entity.BasketProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BasketProductRepo extends JpaRepository<BasketProductEntity, Integer> {

    List<BasketProductEntity> findAllByBasketId(BasketEntity basketId);

}

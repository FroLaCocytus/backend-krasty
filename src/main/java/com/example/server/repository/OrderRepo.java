package com.example.server.repository;

import com.example.server.entity.OrderEntity;
import com.example.server.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<OrderEntity, Integer> {

    OrderEntity findByUserIdAndStatusNot(UserEntity userId, String status);

    Optional<OrderEntity> findByUserId(UserEntity id);


    Page<OrderEntity> findAllByStatus(String status, Pageable pageable);
}

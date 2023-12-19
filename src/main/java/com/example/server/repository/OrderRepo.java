package com.example.server.repository;

import com.example.server.entity.OrderEntity;
import com.example.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<OrderEntity, Integer> {

    OrderEntity findByUserIdAndStatusNot(UserEntity userId, String status);

    List<OrderEntity> findAllByStatusIn(List<String> statuses);

    Optional<OrderEntity> findByUserId(UserEntity id);
}

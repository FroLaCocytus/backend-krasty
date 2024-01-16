package com.example.server.repository;

import com.example.server.entity.OrderEntity;
import com.example.server.entity.UserEntity;
import com.example.server.entity.UserOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOrderRepo extends JpaRepository<UserOrderEntity, Integer> {

    List<UserOrderEntity> findByUserId(UserEntity user);

    @Query("SELECT uo FROM UserOrderEntity uo WHERE uo.userId <> :userId AND uo.orderId = :orderId")
    List<UserOrderEntity> findAllByOrderAndNotUser(OrderEntity orderId, UserEntity userId);

    void deleteByOrderId(OrderEntity order);
}

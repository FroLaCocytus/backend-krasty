package com.example.server.service;

import com.example.server.entity.DocumentEntity;
import com.example.server.entity.OrderEntity;
import com.example.server.entity.UserEntity;
import com.example.server.repository.OrderRepo;
import com.example.server.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    //Можно добавить обработчики ошибок
    public Boolean createOrder(Map<String, Object> request) {

        String orderDescription = (String) request.get("description");
        String userLogin = (String) request.get("login");

        if (orderDescription == null || userLogin == null) {
            return false;
        }

        UserEntity userDB = userRepo.findByLogin(userLogin);

        if (userDB == null) {
            return false;
        }

        OrderEntity orderDB = orderRepo.findByUserIdAndStatusNot(userDB, "complited");

        if (orderDB == null) {
            orderRepo.save(new OrderEntity(orderDescription, "created", userDB));
            return true;
        } else {
            return false;
        }
    }

    public Boolean updateStatus(Map<String, Object> request) {

        String orderStatus = (String) request.get("status");
        String userLogin = (String) request.get("login");

        if (userLogin == null || orderStatus == null) {
            System.out.println("status или login равен нулю");
            return false;
        }

        UserEntity userDB = userRepo.findByLogin(userLogin);

        if (userDB == null) {
            System.out.println("Пользователя не существует");
            return false;
        }

        OrderEntity orderDB = orderRepo.findByUserIdAndStatusNot(userDB, "complited");

        if (orderDB != null) {
            orderDB.setStatus(orderStatus);
            orderRepo.save(orderDB);
            return true;
        } else {
            System.out.println("Заказ не найден");
            return false;
        }
    }

    public Object getAllByRole(Map<String, Object> request) {

        String userRole = (String) request.get("role");

        if (userRole == null) {
            System.out.println("role равно нулю");
            return false;
        }

        if ("cashier".equals(userRole)) {
            List<String> statuses = Arrays.asList("created", "accepted");
            List<OrderEntity> listOrderDB = StreamSupport.stream(orderRepo.findAllByStatusIn(statuses).spliterator(), false)
                    .collect(Collectors.toList());
            return listOrderDB;
        }

        System.out.println("неизвестная ошибка");

        return false;
    }
}

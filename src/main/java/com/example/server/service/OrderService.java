package com.example.server.service;

import com.example.server.entity.OrderEntity;
import com.example.server.entity.UserEntity;
import com.example.server.exception.UniversalException;
import com.example.server.repository.*;
import com.example.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final JwtTokenProvider jwtTokenProvider;


    @Autowired
    public OrderService(UserRepo userRepo,
                        OrderRepo orderRepo,
                        JwtTokenProvider jwtTokenProvider) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    public Boolean createOrder(Map<String, Object> request) {
//
//        String orderDescription = (String) request.get("description");
//        String userLogin = (String) request.get("login");
//
//        if (orderDescription == null || userLogin == null) {
//            return false;
//        }
//
//        UserEntity userDB = userRepo.findByLogin(userLogin);
//
//        if (userDB == null) {
//            return false;
//        }
//
//        OrderEntity orderDB = orderRepo.findByUserIdAndStatusNot(userDB, "complited");
//
//        if (orderDB == null) {
//            orderRepo.save(new OrderEntity(orderDescription, "created", userDB));
//            return true;
//        } else {
//            return false;
//        }
//    }

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

    public Map<String, Object> getOne(String token) throws UniversalException {

        String userLogin = jwtTokenProvider.getLoginFromToken(token);
        UserEntity userDB = userRepo.findByLogin(userLogin);
        if (userDB == null) {
            throw new UniversalException("Пользователь с логином " + userLogin + " не найден.");
        }

        Optional<OrderEntity> orderOptional = orderRepo.findByUserId(userDB);
        Map<String, Object> response = new HashMap<>();
        if (orderOptional.isPresent()) {
            OrderEntity order = orderOptional.get();
            response.put("description", order.getDescription());
            response.put("status", order.getStatus());
        } else {
            response.put("message", "Заказ не найден");
        }
        return response;
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

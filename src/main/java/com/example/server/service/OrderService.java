package com.example.server.service;

import com.example.server.entity.OrderEntity;
import com.example.server.entity.RoleDocumentEntity;
import com.example.server.entity.UserEntity;
import com.example.server.entity.WarehouseEntity;
import com.example.server.exception.UniversalException;
import com.example.server.repository.*;
import com.example.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public void updateStatus(String token, Map<String, Object> request) throws UniversalException {

        Integer orderId = (Integer) request.get("id");
        String orderStatus = (String) request.get("status");
        String userRole = jwtTokenProvider.getRoleFromToken(token);

        Optional<OrderEntity> optionalOrderEntity = orderRepo.findById(orderId);
        if (optionalOrderEntity.isEmpty()) {
            throw new UniversalException("Заказ не найден");
        }
        OrderEntity orderDB = optionalOrderEntity.get();


        switch (orderStatus) {
            case "accepted":
                if (!"cashier".equals(userRole)) {
                    throw new UniversalException("Только кассир может изменить статус на 'accepted'");
                }
                break;
            case "delivering":
                if (!"junior_chef".equals(userRole)) {
                    throw new UniversalException("Только младший повар может изменить статус на 'delivering'");
                }
                break;
            default:
                throw new UniversalException("Неверный статус заказа");
        }

        orderDB.setStatus(orderStatus);
        orderRepo.save(orderDB);
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

    public Map<String, Object> getAllCreated(String token, int page, int size, String sortBy) throws UniversalException {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        String userRole = jwtTokenProvider.getRoleFromToken(token);
        if (!userRole.equals("cashier")) {
            throw new UniversalException("У вас нет доступа к этому действию");
        }
        Page<OrderEntity> orderPage = orderRepo.findAllByStatus("created", pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderPage.getContent());
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());
        return response;
    }

//    public Map<String, Object> getAllAccepted(String token) throws UniversalException {
//        String userRole = jwtTokenProvider.getRoleFromToken(token);
//        if (!userRole.equals("junior chef")){
//            throw new UniversalException("У вас нету доступа к этому действию");
//        }
//        List<OrderEntity> orders = orderRepo.findAllByStatus("accepted");
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("orders", orders);
//        return response;
//    }
}

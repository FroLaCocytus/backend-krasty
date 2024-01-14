package com.example.server.controller;

import com.example.server.exception.UniversalException;
import com.example.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

//    @PostMapping("/create")
//    public ResponseEntity createOrder(@RequestBody Map<String, Object> request){
//        try {
//            return ResponseEntity.ok().body("{\"successfully\": \"" + orderService.createOrder(request) + "\"}");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PutMapping("/update")
    public ResponseEntity updateOrder(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody Map<String, Object> request){
        try {
            String token = authorizationHeader.substring(7);
            orderService.updateStatus(token, request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/one")
    public ResponseEntity getOne(@RequestHeader("Authorization") String authorizationHeader){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(orderService.getOne(token));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAll(@RequestHeader("Authorization") String authorizationHeader,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "status", defaultValue = "status") String status,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam(value = "sort", defaultValue = "id") String sort){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(orderService.getAll(token, page, size, sort, status));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @GetMapping("/accepted")
//    public ResponseEntity getAllAccepted(@RequestHeader("Authorization") String authorizationHeader){
//        try {
//            String token = authorizationHeader.substring(7);
//            return ResponseEntity.ok().body(orderService.getAllAccepted(token));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
}

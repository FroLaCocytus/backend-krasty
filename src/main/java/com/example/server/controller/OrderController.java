package com.example.server.controller;

import com.example.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity createOrder(@RequestBody Map<String, Object> request){
        try {
            return ResponseEntity.ok().body("{\"successfully\": \"" + orderService.createOrder(request) + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update")
    public ResponseEntity updateOrder(@RequestBody Map<String, Object> request){
        try {
            return ResponseEntity.ok().body("{\"successfully\": \"" + orderService.updateStatus(request) + "\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/all")
    public ResponseEntity getAllByRole(@RequestBody Map<String, Object> request){
        try {
            return ResponseEntity.ok().body(orderService.getAllByRole(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

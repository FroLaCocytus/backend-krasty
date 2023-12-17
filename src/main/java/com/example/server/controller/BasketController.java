package com.example.server.controller;

import com.example.server.exception.OrderAlreadyCreatedException;
import com.example.server.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/basket")
@CrossOrigin
public class BasketController {

    @Autowired
    private BasketService basketService;

    @PostMapping("/add")
    public ResponseEntity createAll(@RequestBody Map<String, Object> request){
        try {
            return ResponseEntity.ok().body("{\"successfully\": \"" + basketService.createAll(request) + "\"}");
        } catch (OrderAlreadyCreatedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

//System.out.println();

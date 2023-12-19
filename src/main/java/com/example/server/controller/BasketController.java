package com.example.server.controller;

import com.example.server.exception.OrderAlreadyCreatedException;
import com.example.server.exception.UniversalException;
import com.example.server.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/basket")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @PostMapping("/add")
    public ResponseEntity create(@RequestHeader("Authorization") String authorizationHeader,
                                 @RequestBody Map<String, Object> request){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(basketService.create(token, request));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (OrderAlreadyCreatedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

//System.out.println();

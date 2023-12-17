package com.example.server.controller;

import com.example.server.entity.UserEntity;
import com.example.server.exception.*;
import com.example.server.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Autowired
    private UserService userService;

    /*Функция регистрации нового пользователя*/
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody Map<String, Object> request){
        try {
            return ResponseEntity.ok().body(userService.registration(request));
        } catch (UserAlreadyExistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UserInvalidDataException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    /*Функция аутентификации пользователя*/
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity user) {
        try {
            return ResponseEntity.ok().body(userService.login(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UserInvalidDataException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UserIncorrectPasswordException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка" + e);
        }
    }

    @GetMapping("/auth")
    public ResponseEntity<?> check(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        try {
            return ResponseEntity.ok().body(userService.check(token));
        } catch (UserUnauthorizedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка" + e);
        }
    }

    @GetMapping("/info")
    public ResponseEntity getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        try {
            return ResponseEntity.ok().body(userService.getUserInfo(token));
        } catch (UserUnauthorizedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка" + e);
        }
    }


    @PutMapping("/info")
    public ResponseEntity updateUserInfo(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestBody UserEntity request) {
        String token = authorizationHeader.substring(7);
        try {
            return ResponseEntity.ok().body(userService.updateUserInfo(token, request));
        } catch (UserUnauthorizedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка" + e);
        }
    }

}
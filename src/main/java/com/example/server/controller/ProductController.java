package com.example.server.controller;

import com.example.server.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping
    public ResponseEntity getAll(){
        try {
            return ResponseEntity.ok().body(productService.getAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка: " + e);
        }
    }

}
package com.example.server.service;

import com.example.server.entity.BasketProductEntity;
import com.example.server.entity.ProductEntity;
import com.example.server.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    public List<ProductEntity> getAll() {

        List<ProductEntity> products = StreamSupport.stream(productRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
        
        return products;
    }
}

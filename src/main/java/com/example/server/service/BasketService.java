package com.example.server.service;

import com.example.server.entity.BasketEntity;
import com.example.server.entity.BasketProductEntity;
import com.example.server.entity.ProductEntity;
import com.example.server.entity.UserEntity;
import com.example.server.exception.OrderAlreadyCreatedException;
import com.example.server.repository.BasketProductRepo;
import com.example.server.repository.BasketRepo;
import com.example.server.repository.ProductRepo;
import com.example.server.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BasketService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BasketRepo basketRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private BasketProductRepo basketProductRepo;

    public Boolean createAll(Map<String, Object> request) throws OrderAlreadyCreatedException {

        List<Map<String, Object>> listBasketProduct = (List<Map<String, Object>>) request.get("listProduct");
        String userLogin = (String) request.get("login");

        UserEntity userDB = userRepo.findByLogin(userLogin);
        BasketEntity basketDB = basketRepo.findByUserId(userDB);

        if (basketProductRepo.findAllByBasketId(basketDB).size()>0){
            throw new OrderAlreadyCreatedException("У вас уже есть заказ");
        }

        for (Map<String, Object> map : listBasketProduct) {
            Integer productId = (Integer) map.get("id");
            Integer count = (Integer) map.get("count");
            Optional<ProductEntity> optionalProductEntity = productRepo.findById(productId);
            ProductEntity productDB = optionalProductEntity.get();

            BasketProductEntity basketProduct = new BasketProductEntity(count, productDB, basketDB);
            basketProductRepo.save(basketProduct);
        }

        return true;
    }
}

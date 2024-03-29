package com.example.server.service;

import com.example.server.entity.*;
import com.example.server.exception.OrderAlreadyCreatedException;
import com.example.server.exception.UniversalException;
import com.example.server.repository.*;
import com.example.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasketService {

    private final UserRepo userRepo;
    private final BasketRepo basketRepo;
    private final ProductRepo productRepo;
    private final BasketProductRepo basketProductRepo;
    private final OrderRepo orderRepo;

    private final UserOrderRepo userOrderRepo;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public BasketService(UserRepo userRepo,
                              BasketRepo basketRepo,
                              ProductRepo productRepo,
                              BasketProductRepo basketProductRepo,
                              OrderRepo orderRepo,
                              UserOrderRepo userOrderRepo,
                              JwtTokenProvider jwtTokenProvider) {
        this.userRepo = userRepo;
        this.basketRepo = basketRepo;
        this.productRepo = productRepo;
        this.basketProductRepo = basketProductRepo;
        this.orderRepo = orderRepo;
        this.userOrderRepo = userOrderRepo;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public Map<String, Object> create(String token, Map<String, Object> request)
            throws OrderAlreadyCreatedException, UniversalException {

        String userLogin = jwtTokenProvider.getLoginFromToken(token);
        List<Map<String, Object>> listBasketProduct = (List<Map<String, Object>>) request.get("listProduct");

        UserEntity userDB = userRepo.findByLogin(userLogin);
        if (userDB == null) {
            throw new UniversalException("Пользователь с логином " + userLogin + " не найден.");
        }
        BasketEntity basketDB = basketRepo.findByUserId(userDB);
        if (basketDB == null) {
            throw new UniversalException("Корзина для пользователя с логином " + userLogin + " не найдена.");
        }

        List<UserOrderEntity> userOrders = userOrderRepo.findByUserId(userDB);
        if (!userOrders.isEmpty()) {
            throw new OrderAlreadyCreatedException("У вас уже есть заказ");
        }

        // Формируем описание заказа
        String orderDescription = listBasketProduct.stream()
                .map(productInfo -> {
                    Integer productId = (Integer) productInfo.get("id");
                    Integer count = (Integer) productInfo.get("count");

                    Optional<ProductEntity> optionalProduct = productRepo.findById(productId);
                    if (!optionalProduct.isPresent()) {
                        try {
                            throw new UniversalException("Продукт с ID " + productId + " не найден.");
                        } catch (UniversalException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    ProductEntity product = optionalProduct.get();
                    return product.getName() + " - " + count;
                })
                .collect(Collectors.joining(", "));

        // Создание и сохранение нового заказа с описанием
        OrderEntity order = new OrderEntity(orderDescription, "created");
        OrderEntity orderDB = orderRepo.save(order);

        // Сохранение связи между user и order
        UserOrderEntity userOrder = new UserOrderEntity(userDB, orderDB);
        userOrderRepo.save(userOrder);

        // Проход по всем продуктам в корзине и создание сущностей BasketProductEntity
        for (Map<String, Object> productInfo : listBasketProduct) {
            Integer productId = (Integer) productInfo.get("id");
            Integer count = (Integer) productInfo.get("count");

            Optional<ProductEntity> optionalProduct = productRepo.findById(productId);
            if (!optionalProduct.isPresent()) {
                throw new UniversalException("Продукт с ID " + productId + " не найден.");
            }
            ProductEntity product = optionalProduct.get();

            BasketProductEntity basketProduct = new BasketProductEntity(count, product, basketDB);
            basketProductRepo.save(basketProduct);
        }

        // Создаем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("title", listBasketProduct);
        return response;
    }

    public Map<String, Object> getOne(String token) throws UniversalException {
        String userLogin = jwtTokenProvider.getLoginFromToken(token);

        UserEntity userDB = userRepo.findByLogin(userLogin);
        if (userDB == null) {
            throw new UniversalException("Пользователь с логином " + userLogin + " не найден.");
        }

        // Получаем корзину пользователя
        BasketEntity userBasket = basketRepo.findByUserId(userDB);
        if (userBasket == null) {
            throw new UniversalException("Корзина для пользователя " + userLogin + " не найдена.");
        }

        List<BasketProductEntity> basketProducts = userBasket.getManyBasketProduct();

        // Создаем список для хранения продуктов с их информацией
        List<Map<String, Object>> productsList = new ArrayList<>();
        for (BasketProductEntity basketProduct : basketProducts) {
            ProductEntity product = basketProduct.getProductId(); // Получаем продукт

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("name", product.getName()); // Название продукта
            productInfo.put("count", basketProduct.getCount()); // Количество продукта
            productInfo.put("description", product.getDescription()); // Описание продукта
            productInfo.put("img_path", product.getImg_path()); // Путь к изображению продукта

            productsList.add(productInfo); // Добавляем информацию о продукте в список
        }

        // Возвращаем ответ
        Map<String, Object> response = new HashMap<>();
        response.put("products", productsList);
        return response;
    }
}

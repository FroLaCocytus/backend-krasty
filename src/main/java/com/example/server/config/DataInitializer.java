package com.example.server.config;

import com.example.server.controller.UserController;
import com.example.server.entity.ProductEntity;
import com.example.server.entity.RoleEntity;
import com.example.server.entity.WarehouseEntity;
import com.example.server.repository.ProductRepo;
import com.example.server.repository.RoleRepo;
import com.example.server.repository.UserRepo;
import com.example.server.repository.WarehouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Component
public class DataInitializer {
    private final ProductRepo productRepository;
    private final RoleRepo roleRepository;
    private final WarehouseRepo warehouseRepository;
    private final UserRepo userRepository;
    private final UserController userController;
    private final String documentsPath;

    @Autowired
    public DataInitializer(ProductRepo productRepository,
                           RoleRepo roleRepository,
                           WarehouseRepo warehouseRepository,
                           UserRepo userRepository,
                           UserController userController,
                           @Value("${upload.path}") String documentsPath) {
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.userController = userController;
        this.documentsPath = documentsPath;
    }

    @EventListener
    public void initialize(ContextRefreshedEvent event) {
        // Удаляем все файлы из папки
        clearDirectory(Paths.get(documentsPath));

        // Инициализация продуктов
        ProductEntity product1 = new ProductEntity(
                "Крабсбургер",
                "Два куска мяса в нежнейших булочках, посыпанных кунжутом, лист салата и сыр",
                1.5f,
                "img/krabsburger.png"
        );
        ProductEntity product2 = new ProductEntity(
                "Красти-дог",
                "Вкуснейшая сосиска в нежной булочке, покрытая слоем горчицы",
                1.25f,
                "img/krusty_dog.png"
        );
        ProductEntity product3 = new ProductEntity(
                "Кораллы фри",
                "Жаренные во фритюре кораллы с хрустящей корочкой",
                0.75f,
                "img/corals.png"
        );

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // Инициализация ролей
        RoleEntity role1 = new RoleEntity(
                "client"
        );
        RoleEntity role2 = new RoleEntity(
                "accountant"
        );
        RoleEntity role3 = new RoleEntity(
                "merchandiser"
        );
        RoleEntity role4 = new RoleEntity(
                "cashier"
        );
        RoleEntity role5 = new RoleEntity(
                "chef"
        );
        RoleEntity role6 = new RoleEntity(
                "junior chef"
        );
        RoleEntity role7 = new RoleEntity(
                "courier"
        );

        roleRepository.save(role1);
        roleRepository.save(role2);
        roleRepository.save(role3);
        roleRepository.save(role4);
        roleRepository.save(role5);
        roleRepository.save(role6);
        roleRepository.save(role7);

        // Инициализация складов
        WarehouseEntity warehouse1 = new WarehouseEntity(
                "Улица Пушкина дом Колотушкина"
        );

        warehouseRepository.save(warehouse1);

        // Инициализация пользователей
        Map<String, Object> user1 = new HashMap<>();
        user1.put("login", "clie");
        user1.put("password", "Aa1425");
        user1.put("role", "client");

        Map<String, Object> user2 = new HashMap<>();
        user2.put("login", "acco");
        user2.put("password", "Aa1425");
        user2.put("role", "accountant");

        Map<String, Object> user3 = new HashMap<>();
        user3.put("login", "merc");
        user3.put("password", "Aa1425");
        user3.put("role", "merchandiser");

        Map<String, Object> user4 = new HashMap<>();
        user4.put("login", "cash");
        user4.put("password", "Aa1425");
        user4.put("role", "cashier");

        Map<String, Object> user5 = new HashMap<>();
        user5.put("login", "chef");
        user5.put("password", "Aa1425");
        user5.put("role", "chef");

        userController.registration(user1);
        userController.registration(user2);
        userController.registration(user3);
        userController.registration(user4);
        userController.registration(user5);
        System.out.println("Инициализация прошла успешно!");
    }

    private void clearDirectory(Path path) {
        try (Stream<Path> files = Files.list(path)) {
            files.forEach(file -> {
                try {
                    if (Files.isRegularFile(file)) {
                        Files.delete(file);
                    }
                } catch (IOException e) {
                    System.err.println("Не удалось удалить файл: " + file + " из-за ошибки: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Ошибка при попытке обойти каталог: " + e.getMessage());
        }
    }
}
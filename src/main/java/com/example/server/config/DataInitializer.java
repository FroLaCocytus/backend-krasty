package com.example.server.config;

import com.example.server.controller.UserController;
import com.example.server.entity.InitializationStatus;
import com.example.server.entity.ProductEntity;
import com.example.server.entity.RoleEntity;
import com.example.server.entity.WarehouseEntity;
import com.example.server.exception.UniversalException;
import com.example.server.exception.UserAlreadyExistException;
import com.example.server.exception.UserInvalidDataException;
import com.example.server.exception.UserUnauthorizedException;
import com.example.server.repository.*;
import com.example.server.security.JwtTokenProvider;
import com.example.server.service.UserService;
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
    private final UserService userService;
    private final String documentsPath;
    private final String imgPath;


    private final JwtTokenProvider jwtTokenProvider;

    private final InitializationStatusRepo initializationStatusRepo;

    @Autowired
    public DataInitializer(ProductRepo productRepository,
                           RoleRepo roleRepository,
                           WarehouseRepo warehouseRepository,
                           UserRepo userRepository,
                           UserService userService,
                           @Value("${document.path}") String documentsPath,
                           @Value("${img.path}") String imgPath,
                           JwtTokenProvider jwtTokenProvider,
                           InitializationStatusRepo initializationStatusRepo) {
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.warehouseRepository = warehouseRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.documentsPath = documentsPath;
        this.imgPath = imgPath;
        this.jwtTokenProvider = jwtTokenProvider;
        this.initializationStatusRepo = initializationStatusRepo;
    }

    @EventListener
    public void initialize(ContextRefreshedEvent event) throws UserAlreadyExistException, UniversalException, UserInvalidDataException, UserUnauthorizedException {
        List<InitializationStatus> initializationStatusList = initializationStatusRepo.findAll();

        if (initializationStatusList.isEmpty() || !initializationStatusList.get(0).isInitialized()) {


            // Удаляем все файлы из папки
            clearDirectory(Paths.get(documentsPath));

            // Инициализация продуктов
            ProductEntity product1 = new ProductEntity(
                    "Крабсбургер",
                    "Два куска мяса в нежнейших булочках, посыпанных кунжутом, лист салата и сыр",
                    1.5f,
                    imgPath + "krabsburger.png"
            );
            ProductEntity product2 = new ProductEntity(
                    "Красти-дог",
                    "Вкуснейшая сосиска в нежной булочке, покрытая слоем горчицы",
                    1.25f,
                    imgPath + "krusty_dog.png"
            );
            ProductEntity product3 = new ProductEntity(
                    "Кораллы фри",
                    "Жаренные во фритюре кораллы с хрустящей корочкой",
                    0.75f,
                    imgPath + "corals.png"
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

            Map<String, Object> user6 = new HashMap<>();
            user6.put("login", "junc");
            user6.put("password", "Aa1425");
            user6.put("role", "junior chef");

            Map<String, Object> user7 = new HashMap<>();
            user7.put("login", "cour");
            user7.put("password", "Aa1425");
            user7.put("role", "courier");

            userService.registration(user1);

            String token = jwtTokenProvider.generateTokenForAccountant();

            userService.registrationStaff(token, user2);
            userService.registrationStaff(token, user3);
            userService.registrationStaff(token, user4);
            userService.registrationStaff(token, user5);
            userService.registrationStaff(token, user6);
            userService.registrationStaff(token, user7);

            InitializationStatus status = new InitializationStatus();
            status.setInitialized(true);
            initializationStatusRepo.save(status);

            System.out.println("Инициализация прошла успешно!");
        } else {
            System.out.println("Инициализация уже была выполнена.");
        }
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
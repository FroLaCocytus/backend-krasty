package com.example.server.service;

import com.example.server.entity.*;
import com.example.server.exception.*;
import com.example.server.repository.BasketRepo;
import com.example.server.repository.RoleRepo;
import com.example.server.repository.UserRepo;

//JWT token и хеширование пароля
import com.example.server.security.JwtTokenProvider;
import org.mindrot.jbcrypt.BCrypt;
import io.jsonwebtoken.Claims;

import com.example.server.repository.WarehouseRepo;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {

    private static final int MIN_LOGIN_LENGTH = 4;
    private static final int MAX_LOGIN_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private final UserRepo userRepo;
    private final BasketRepo basketRepo;
    private final RoleRepo roleRepo;
    private final WarehouseRepo warehouseRepo;
    private final JwtTokenProvider jwtTokenProvider;


    @Autowired
    public UserService(UserRepo userRepo, BasketRepo basketRepo, RoleRepo roleRepo, WarehouseRepo warehouseRepo, JwtTokenProvider jwtTokenProvider) {
        this.userRepo = userRepo;
        this.basketRepo = basketRepo;
        this.roleRepo = roleRepo;
        this.warehouseRepo = warehouseRepo;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private void validateUserEntity(UserEntity user) throws UserInvalidDataException {
        if (user == null) {
            throw new UserInvalidDataException("Некорректные данные");
        }
        if (user.getLogin().length() < MIN_LOGIN_LENGTH || user.getLogin().length() > MAX_LOGIN_LENGTH) {
            throw new UserInvalidDataException("Некорректный логин");
        }
        if (user.getPassword().length() < MIN_PASSWORD_LENGTH || user.getPassword().length() > MAX_PASSWORD_LENGTH) {
            throw new UserInvalidDataException("Некорректный пароль");
        }
    }

    public Map<String, Object> registration(Map<String, Object> request) throws UserAlreadyExistException, UserInvalidDataException {

        String userLogin = (String) request.get("login");
        String userPassword = (String) request.get("password");

        RoleEntity userRole = roleRepo.findByName("client");

        UserEntity user = new UserEntity(userLogin, userPassword, userRole);
        validateUserEntity(user);

        if(userRepo.findByLogin(user.getLogin()) != null) {
            throw new UserAlreadyExistException("Пользователь с таким именем уже существует");
        }

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); //Шифруем пароль для БД
        UserEntity userDB = userRepo.save(user);

        // Создаём для клиента корзину
        BasketEntity basket = new BasketEntity(userDB);
        basketRepo.save(basket);

        // Создаём JWT токен и отправляем ответ с сервера
        String jwtToken = jwtTokenProvider.generateToken(userDB.getId(), userDB.getLogin(), userDB.getRoleId().getName());
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        return response;
    }

    public Map<String, Object> registrationStaff(String token, Map<String, Object> request) throws UserAlreadyExistException,
            UserInvalidDataException, UserUnauthorizedException, UniversalException {

        String creatorRole = jwtTokenProvider.getRoleFromToken(token);
        if (creatorRole == null){
            throw new UserUnauthorizedException("Пользователь не авторизован");
        }
        if (!creatorRole.equals("accountant")){
            throw new UniversalException("У вас нету доступа к этому действию");
        }

        String userLogin = (String) request.get("login");
        String userPassword = (String) request.get("password");
        RoleEntity userRole = roleRepo.findByName((String) request.get("role"));

        UserEntity user = new UserEntity(userLogin, userPassword, userRole);
        validateUserEntity(user);

        if(userRepo.findByLogin(user.getLogin()) != null) {
            throw new UserAlreadyExistException("Пользователь с таким именем уже существует");
        }

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); //Шифруем пароль для БД
        UserEntity userDB = userRepo.save(user);

        // Если пользователь складовщик связываем его со складом (пока что захардкожен 1 склад)
        if(user.getRoleId().getName().equals("merchandiser")) {
            Optional<WarehouseEntity> optionalWarehouseEntity = warehouseRepo.findById(1);
            if (optionalWarehouseEntity.isEmpty()) {
                throw new UniversalException("Склад не найден");
            }
            WarehouseEntity warehouseDB = optionalWarehouseEntity.get();
            warehouseDB.setUserId(userDB);
            warehouseRepo.save(warehouseDB);
        }
        // Создаём JWT токен и отправляем ответ с сервера
        String jwtToken = jwtTokenProvider.generateToken(userDB.getId(), userDB.getLogin(), userDB.getRoleId().getName());
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);

        return response;
    }

    public Map<String, Object> login(UserEntity user) throws UserNotFoundException,
            UserInvalidDataException, UserIncorrectPasswordException {

        validateUserEntity(user);

        UserEntity userDB = userRepo.findByLogin(user.getLogin());

        if(userDB == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (!BCrypt.checkpw(user.getPassword(), userDB.getPassword())){
            throw new UserIncorrectPasswordException("Неправильный пароль");
        }

        // Создаём JWT токен и отправляем ответ с сервера
        String jwtToken = jwtTokenProvider.generateToken(userDB.getId(), userDB.getLogin(), userDB.getRoleId().getName());
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);

        return response;
    }


    public Map<String, Object> check(String token) throws UserUnauthorizedException{

        Claims tokenPayload = jwtTokenProvider.verifyToken(token);
        if (tokenPayload == null){
            throw new UserUnauthorizedException("Пользователь не авторизован");
        }
        UserEntity userDB = userRepo.findByLogin(tokenPayload.get("login", String.class));

        // Создаём JWT токен и отправляем ответ с сервера
        String jwtToken = jwtTokenProvider.generateToken(userDB.getId(), userDB.getLogin(), userDB.getRoleId().getName());
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);

        return response;
    }

    public Map<String, Object> getUserInfo(String token) throws UserUnauthorizedException{

        Claims tokenPayload = jwtTokenProvider.verifyToken(token);
        if (tokenPayload == null){
            throw new UserUnauthorizedException("Пользователь не авторизован");
        }
        UserEntity userDB = userRepo.findByLogin(tokenPayload.get("login", String.class));
        Map<String, Object> response = new HashMap<>();
        response.put("name", userDB.getName());
        response.put("email", userDB.getEmail());
        response.put("phone_number", userDB.getPhone_number());
        response.put("address", userDB.getAddress());

        return response;
    }

    public Map<String, Object> get(String token) throws UserUnauthorizedException{

        Claims tokenPayload = jwtTokenProvider.verifyToken(token);
        if (tokenPayload == null){
            throw new UserUnauthorizedException("Пользователь не авторизован");
        }
        UserEntity userDB = userRepo.findByLogin(tokenPayload.get("login", String.class));
        Map<String, Object> response = new HashMap<>();
        response.put("name", userDB.getName());
        response.put("email", userDB.getEmail());
        response.put("phone_number", userDB.getPhone_number());
        response.put("address", userDB.getAddress());

        return response;
    }

    public Map<String, Object> updateUserInfo(String token, UserEntity request) throws UserUnauthorizedException{

        Claims tokenPayload = jwtTokenProvider.verifyToken(token);

        if (tokenPayload == null){
            throw new UserUnauthorizedException("Пользователь не авторизован");
        }

        UserEntity userDB = userRepo.findByLogin(tokenPayload.get("login", String.class));

        // Проверяем что поле не пустое, если проверка пройдена обновляем значение
        if(!request.getName().isBlank()) {
            userDB.setName(request.getName());
        }
        if(!request.getEmail().isBlank()) {
            userDB.setEmail(request.getEmail());
        }
        if(!request.getPhone_number().isBlank()) {
            userDB.setPhone_number(request.getPhone_number());
        }
        if(!request.getAddress().isBlank()) {
            userDB.setAddress(request.getAddress());
        }

        userDB = userRepo.save(userDB);

        Map<String, Object> response = new HashMap<>();
        response.put("name", userDB.getName());
        response.put("email", userDB.getEmail());
        response.put("phone_number", userDB.getPhone_number());
        response.put("address", userDB.getAddress());

        return response;
    }

}

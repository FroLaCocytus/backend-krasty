package com.example.server.function_testing;
import com.example.server.controller.UserController;
import com.example.server.entity.UserEntity;
import com.example.server.exception.UserAlreadyExistException;
import com.example.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registration_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("login", "testUser");
        request.put("password", "testPass");

        Map<String, Object> response = new HashMap<>();
        response.put("token", "dummyToken");

        given(userService.registration(any())).willReturn(response);

        mockMvc.perform(post("/user/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"dummyToken\"}"));
    }

    @Test
    public void login_Success() throws Exception {
        UserEntity user = new UserEntity();
        user.setLogin("testUser");
        user.setPassword("testPass");

        Map<String, Object> response = new HashMap<>();
        response.put("token", "dummyToken");

        given(userService.login(any(UserEntity.class))).willReturn(response);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"token\":\"dummyToken\"}"));
    }

    // Тест для неудачной регистрации
    @Test
    public void registration_UserAlreadyExists() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("login", "existingUser");
        request.put("password", "testPass");

        given(userService.registration(any())).willThrow(new UserAlreadyExistException("Пользователь с таким именем уже существует"));

        mockMvc.perform(post("/user/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Пользователь с таким именем уже существует")));
    }
}
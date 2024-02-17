package com.example.server.function_testing;

import com.example.server.controller.UserController;
import com.example.server.entity.UserEntity;
import com.example.server.exception.UserUnauthorizedException;
import com.example.server.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;



@WebMvcTest(UserController.class)
public class UserProfileTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void updateUserInfo_Success() throws Exception {
        // Создаем тестового пользователя с данными для обновления
        UserEntity user = new UserEntity();
        user.setName("Test Name");
        user.setEmail("test@example.com");
        user.setPhone_number("1234567890");
        user.setAddress("Test Address");
        String token = "Bearer validToken";

        // Подготовка ожидаемого ответа
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("name", user.getName());
        expectedResponse.put("email", user.getEmail());
        expectedResponse.put("phone_number", user.getPhone_number());
        expectedResponse.put("address", user.getAddress());

        given(userService.updateUserInfo(anyString(), any(UserEntity.class))).willReturn(expectedResponse);

        mockMvc.perform(put("/user/info")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }


    @Test
    public void updateUserInfo_Unauthorized() throws Exception {
        UserEntity user = new UserEntity(); // Заполните необходимые поля
        String token = "Bearer dummyToken";

        given(userService.updateUserInfo(anyString(), any(UserEntity.class)))
                .willThrow(new UserUnauthorizedException("Пользователь не авторизован"));

        mockMvc.perform(put("/user/info")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Пользователь не авторизован")));
    }

}


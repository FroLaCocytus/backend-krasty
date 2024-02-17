package com.example.server.function_testing;

import com.example.server.controller.OrderController;
import com.example.server.exception.UniversalException;
import com.example.server.service.OrderService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(OrderController.class)
public class CourierOrderInteractionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getOneOrderCourier_Success() throws Exception {
        String token = "Bearer validToken";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("id", 1);
        expectedResponse.put("description", "Test Order");
        expectedResponse.put("clientName", "Test Client");
        expectedResponse.put("clientNumber", "1234567890");
        expectedResponse.put("deliveryAddress", "Test Address");

        given(orderService.getOneOrderCourier(anyString())).willReturn(expectedResponse);

        mockMvc.perform(get("/order/courier")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void getOneOrderCourier_Unauthorized() throws Exception {
        String token = "Bearer invalidToken";

        given(orderService.getOneOrderCourier(anyString()))
                .willThrow(new UniversalException("Пользователь не авторизован"));

        mockMvc.perform(get("/order/courier")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Пользователь не авторизован")));
    }
}

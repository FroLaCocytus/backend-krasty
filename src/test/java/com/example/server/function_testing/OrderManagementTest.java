package com.example.server.function_testing;

import com.example.server.controller.OrderController;
import com.example.server.entity.OrderEntity;
import com.example.server.exception.UniversalException;
import com.example.server.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(OrderController.class)
public class OrderManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllOrders_Success() throws Exception {
        String token = "Bearer validToken";
        int page = 0;
        int size = 10;
        String sortBy = "id";
        String status = "created";

        Pageable pageable = PageRequest.of(page, size);
        OrderEntity order = new OrderEntity("Test Order", status);
        PageImpl<OrderEntity> orderPage = new PageImpl<>(Collections.singletonList(order), pageable, 1);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("orders", orderPage.getContent());
        expectedResponse.put("currentPage", orderPage.getNumber());
        expectedResponse.put("totalItems", orderPage.getTotalElements());
        expectedResponse.put("totalPages", orderPage.getTotalPages());

        given(orderService.getAll(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(expectedResponse);

        mockMvc.perform(get("/order/all")
                        .header("Authorization", token)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", sortBy)
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void getAllOrders_Unauthorized() throws Exception {
        String token = "Bearer invalidToken";

        given(orderService.getAll(anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .willThrow(new UniversalException("Пользователь не авторизован"));

        mockMvc.perform(get("/order/all")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Пользователь не авторизован")));
    }

    @Test
    public void updateOrder_Success() throws Exception {
        String token = "Bearer validToken";
        Map<String, Object> request = new HashMap<>();
        request.put("id", 1);
        request.put("status", "accepted");

        mockMvc.perform(put("/order/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateOrder_Unauthorized() throws Exception {
        String token = "Bearer invalidToken";
        Map<String, Object> request = new HashMap<>();
        request.put("id", 1);
        request.put("status", "accepted");

        doThrow(new UniversalException("Пользователь не авторизован"))
                .when(orderService).updateStatus(anyString(), any(Map.class));


        mockMvc.perform(put("/order/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Пользователь не авторизован")));
    }
}


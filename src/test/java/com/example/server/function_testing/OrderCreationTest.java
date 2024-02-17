package com.example.server.function_testing;

import com.example.server.controller.BasketController;
import com.example.server.controller.OrderController;
import com.example.server.entity.OrderEntity;
import com.example.server.entity.UserEntity;
import com.example.server.entity.UserOrderEntity;
import com.example.server.exception.OrderAlreadyCreatedException;
import com.example.server.service.BasketService;
import com.example.server.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest({BasketController.class, OrderController.class})
public class OrderCreationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BasketService basketService;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createOrder_Success() throws Exception {
        Map<String, Object> request = new HashMap<>();
        List<Map<String, Object>> listProduct = List.of(
                Map.of("id", 1, "count", 2),
                Map.of("id", 2, "count", 1)
        );
        request.put("listProduct", listProduct);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("title", listProduct);

        given(basketService.create(any(String.class), any(Map.class))).willReturn(expectedResponse);

        mockMvc.perform(post("/basket/add")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void createOrder_OrderAlreadyCreated() throws Exception {
        Map<String, Object> request = new HashMap<>();
        given(basketService.create(any(String.class), any(Map.class)))
                .willThrow(new OrderAlreadyCreatedException("У вас уже есть заказ"));

        mockMvc.perform(post("/basket/add")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("У вас уже есть заказ")));
    }

    @Test
    public void getOneOrder_Success() throws Exception {
        UserEntity user = new UserEntity();
        user.setLogin("testUser");
        String token = "Bearer validToken";

        OrderEntity order = new OrderEntity("Test Order", "created");

        UserOrderEntity userOrder = new UserOrderEntity(user, order);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("id", order.getId());
        expectedResponse.put("description", order.getDescription());
        expectedResponse.put("status", order.getStatus());

        given(orderService.getOne(anyString())).willReturn(expectedResponse);

        mockMvc.perform(get("/order/one")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void getOneOrder_NoOrdersFound() throws Exception {
        UserEntity user = new UserEntity();
        user.setLogin("testUser");
        String token = "Bearer validToken";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Заказ не найден");

        given(orderService.getOne(anyString())).willReturn(expectedResponse);

        mockMvc.perform(get("/order/one")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }
}
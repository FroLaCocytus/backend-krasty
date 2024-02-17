package com.example.server.function_testing;

import com.example.server.controller.MerchandiseController;
import com.example.server.entity.MerchandiseEntity;
import com.example.server.exception.UniversalException;
import com.example.server.service.MerchandiseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(MerchandiseController.class)
public class MerchandiseManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MerchandiseService merchandiseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createMerchandise_Success() throws Exception {
        String token = "Bearer validToken";
        MerchandiseEntity merchandise = new MerchandiseEntity();
        merchandise.setTitle("Test Merchandise");
        merchandise.setCount(10);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("title", merchandise.getTitle());
        expectedResponse.put("count", merchandise.getCount());

        given(merchandiseService.createMerchandise(anyString(), any(MerchandiseEntity.class)))
                .willReturn(expectedResponse);

        mockMvc.perform(post("/merchandise/create")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchandise)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void createMerchandise_Unauthorized() throws Exception {
        String token = "Bearer invalidToken";
        MerchandiseEntity merchandise = new MerchandiseEntity(); // Заполните необходимые поля

        given(merchandiseService.createMerchandise(anyString(), any(MerchandiseEntity.class)))
                .willThrow(new UniversalException("Пользователь не авторизован"));

        mockMvc.perform(post("/merchandise/create")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchandise)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Пользователь не авторизован")));
    }

    @Test
    public void updateMerchandise_Success() throws Exception {
        String token = "Bearer validToken";
        MerchandiseEntity merchandise = new MerchandiseEntity();
        merchandise.setId(1);
        merchandise.setTitle("Updated Merchandise");
        merchandise.setCount(20);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("title", merchandise.getTitle());

        given(merchandiseService.updateMerchandise(anyString(), any(MerchandiseEntity.class)))
                .willReturn(expectedResponse);

        mockMvc.perform(put("/merchandise/update")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchandise)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void deleteMerchandise_Success() throws Exception {
        String token = "Bearer validToken";
        Integer merchandiseId = 1;

        mockMvc.perform(delete("/merchandise/delete")
                        .header("Authorization", token)
                        .param("id", String.valueOf(merchandiseId)))
                .andExpect(status().isOk());
    }

    @Test
    public void getOneMerchandise_Success() throws Exception {
        String token = "Bearer validToken";
        MerchandiseEntity merchandise = new MerchandiseEntity();
        merchandise.setId(1);

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("title", "Test Merchandise");

        given(merchandiseService.getOne(anyString(), any(MerchandiseEntity.class)))
                .willReturn(expectedResponse);

        mockMvc.perform(post("/merchandise/one")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(merchandise)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void getAllMerchandise_Success() throws Exception {
        String token = "Bearer validToken";

        Page<MerchandiseEntity> merchandisePage = new PageImpl<>(new ArrayList<>());
        given(merchandiseService.getAll(anyString(), anyInt(), anyInt(), anyString()))
                .willReturn(merchandisePage);

        mockMvc.perform(get("/merchandise/all")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }
}

package com.example.server.function_testing;

import com.example.server.controller.ProductController;
import com.example.server.entity.ProductEntity;
import com.example.server.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
public class MenuInteractionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void getAllProducts_Success() throws Exception {
        List<ProductEntity> products = Arrays.asList(
                new ProductEntity("Apple", "Fresh apples", 2.5f, "apple.jpg"),
                new ProductEntity("Banana", "Sweet bananas", 1.5f, "banana.jpg")
        );

        Mockito.when(productService.getAll()).thenReturn(products);

        mockMvc.perform(get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{'name':'Apple'},{'name':'Banana'}]"));
    }

    @Test
    public void getAllProducts_Error() throws Exception {
        Mockito.when(productService.getAll()).thenThrow(new RuntimeException("Internal server error"));

        mockMvc.perform(get("/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Произошла ошибка: java.lang.RuntimeException: Internal server error")));
    }
}


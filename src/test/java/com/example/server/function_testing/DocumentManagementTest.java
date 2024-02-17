package com.example.server.function_testing;

import com.example.server.controller.DocumentController;
import com.example.server.entity.DocumentEntity;
import com.example.server.exception.UniversalException;
import com.example.server.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
public class DocumentManagementTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createDocument_Success() throws Exception {
        String token = "Bearer validToken";
        MockMultipartFile file = new MockMultipartFile("file",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String description = "Test Document";
        String roles = "role1,role2";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("title", "test.txt");
        expectedResponse.put("description", "Test Document");
        expectedResponse.put("date", "2024-02-16");
        expectedResponse.put("path", "doc/test.txt");
        expectedResponse.put("roles", "[role1, role2]");

        given(documentService.createDocument(anyString(), any(), anyString(), anyString())).willReturn(expectedResponse);

        mockMvc.perform(multipart("/document/create")
                        .file(file)
                        .param("description", description)
                        .param("roles", roles)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void createDocument_AccessDenied() throws Exception {
        String token = "Bearer invalidToken";
        MockMultipartFile file = new MockMultipartFile("file",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        String description = "Test Document";
        String roles = "role1,role2";

        given(documentService.createDocument(anyString(), any(), anyString(), anyString()))
                .willThrow(new UniversalException("У вас нету доступа к этому действию"));

        mockMvc.perform(multipart("/document/create")
                        .file(file)
                        .param("description", description)
                        .param("roles", roles)
                        .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("У вас нету доступа к этому действию"));
    }

    @Test
    public void updateDocument_Success() throws Exception {
        String token = "Bearer validToken";
        Integer documentId = 1;
        String description = "Updated Document";
        String roles = "role1,role2";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("title", "test.txt");
        expectedResponse.put("description", "Updated Document");
        expectedResponse.put("date", "2024-02-16");
        expectedResponse.put("path", "doc/test.txt");
        expectedResponse.put("roles", "[role1, role2]");

        given(documentService.updateDocument(anyString(), anyInt(), anyString(), anyString())).willReturn(expectedResponse);

        mockMvc.perform(put("/document/update")
                        .param("id", String.valueOf(documentId))
                        .param("description", description)
                        .param("roles", roles)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void deleteDocument_Success() throws Exception {
        String token = "Bearer validToken";
        Integer documentId = 1;

        mockMvc.perform(delete("/document/delete")
                        .param("id", String.valueOf(documentId))
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    public void getOneDocument_Success() throws Exception {
        Integer documentId = 1;
        String token = "Bearer validToken";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("title", "test.txt");
        expectedResponse.put("description", "Test Document");
        expectedResponse.put("date", "2024-02-16");
        expectedResponse.put("path", "doc/test.txt");
        expectedResponse.put("roles", "[role1, role2]");

        given(documentService.getOne(anyInt())).willReturn(expectedResponse);

        mockMvc.perform(post("/document/one")
                        .param("id", String.valueOf(documentId))
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void getAllDocuments_Success() throws Exception {
        String token = "Bearer validToken";
        DocumentEntity document1 = new DocumentEntity();
        document1.setId(1);
        document1.setTitle("test1.txt");
        document1.setDescription("Test Document 1");
        document1.setDate(LocalDate.now());
        document1.setPath("doc/test1.txt");

        DocumentEntity document2 = new DocumentEntity();
        document2.setId(2);
        document2.setTitle("test2.txt");
        document2.setDescription("Test Document 2");
        document2.setDate(LocalDate.now());
        document2.setPath("doc/test2.txt");

        List<DocumentEntity> documents = Arrays.asList(document1, document2);

        Page<Map<String, Object>> page = new PageImpl<>(
                documents.stream().map(document -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", document.getId());
                    response.put("title", document.getTitle());
                    response.put("description", document.getDescription());
                    response.put("date", document.getDate());
                    response.put("path", document.getPath());
                    response.put("roles", Collections.singletonList("accountant"));
                    return response;
                }).collect(Collectors.toList())
        );

        given(documentService.getAll(anyString(), anyInt(), anyInt(), anyString())).willReturn(page);

        mockMvc.perform(get("/document/all")
                        .header("Authorization", token))
                .andExpect(status().isOk());
    }


}

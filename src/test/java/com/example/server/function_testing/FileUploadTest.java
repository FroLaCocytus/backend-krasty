package com.example.server.function_testing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.server.controller.DocumentController;
import com.example.server.exception.UniversalException;
import com.example.server.service.DocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(DocumentController.class)
public class FileUploadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @Test
    public void downloadFile_Success() throws Exception {
        String token = "Bearer validToken";
        Integer fileId = 1;

        // Заглушка для успешного сценария
        when(documentService.downloadDocument(any(), any()))
                .thenReturn(new ByteArrayResource("Test file content".getBytes()));

        mockMvc.perform(get("/document/download")
                        .param("id", String.valueOf(fileId))
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().string("Test file content"));
    }

    @Test
    public void downloadFile_AccessDenied() throws Exception {
        String token = "Bearer validToken";
        Integer fileId = 1;

        // Заглушка для сценария с ошибкой доступа
        when(documentService.downloadDocument(any(), any()))
                .thenThrow(new UniversalException("У вас нету доступа к этому документу"));

        mockMvc.perform(get("/document/download")
                        .param("id", String.valueOf(fileId))
                        .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("У вас нету доступа к этому документу"));
    }
}

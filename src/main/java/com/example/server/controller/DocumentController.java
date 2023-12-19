package com.example.server.controller;

import com.example.server.exception.UniversalException;
import com.example.server.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/create")
    public ResponseEntity createDocument(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestParam("file") MultipartFile file,
                                         @RequestParam("description") String description,
                                         @RequestParam("roles") String roles){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(documentService.createDocument(token, file, description, roles));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateDocument(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestParam("id") Integer id,
                                         @RequestParam("description") String description,
                                         @RequestParam("roles") String roles){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(documentService.updateDocument(token, id, description, roles));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteDocument(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestParam("id") Integer id,
                                         @RequestParam(value = "role", defaultValue = "client") String role){
        try {
            String token = authorizationHeader.substring(7);
            documentService.deleteDocument(token, id);
            return ResponseEntity.ok().build();
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PostMapping("/one")
    public ResponseEntity getOne(@RequestHeader("Authorization") String authorizationHeader,
                                 @RequestParam("id") Integer id){
        try {
            return ResponseEntity.ok().body(documentService.getOne(id));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @GetMapping("/all")
    public ResponseEntity getAll(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(documentService.getAll(token, page, size, sort));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @GetMapping("/download")
    public ResponseEntity downloadDocument(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("id") Integer documentId){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(documentService.downloadDocument(token, documentId));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка" + e);
        }
    }
}

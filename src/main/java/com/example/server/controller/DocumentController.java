package com.example.server.controller;

import com.example.server.exception.UniversalException;
import com.example.server.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/document")
@CrossOrigin
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/create")
    public ResponseEntity createDocument(@RequestParam("file") MultipartFile file,
                                      @RequestParam("description") String description,
                                      @RequestParam(value = "role", defaultValue = "client") String role,
                                      @RequestParam("roles") String roles){
        try {
            return ResponseEntity.ok().body(documentService.createDocument(file, description, role, roles));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateDocument(@RequestParam("id") Integer id,
                                         @RequestParam("description") String description,
                                         @RequestParam(value = "role", defaultValue = "client") String role,
                                         @RequestParam("roles") String roles){
        System.out.println(id);
        System.out.println(description);
        System.out.println(roles);

        try {
            return ResponseEntity.ok().body(documentService.updateDocument(id, description, role, roles));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteDocument(@RequestParam("id") Integer id,
                                         @RequestParam(value = "role", defaultValue = "client") String role){
        try {
            documentService.deleteDocument(id, role);
            return ResponseEntity.ok().build();
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PostMapping("/one")
    public ResponseEntity getOne(@RequestParam("id") Integer id){
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
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "role", defaultValue = "client") String role){
        try {
            return ResponseEntity.ok().body(documentService.getAll(page, size, sort, role));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @GetMapping("/all/role")
    public ResponseEntity getAllByRole(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "role", defaultValue = "client") String role){
        try {
            return ResponseEntity.ok().body(documentService.getAllByRole(page, size, sort, role));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @GetMapping("/download")
    public ResponseEntity downloadDocument(
            @RequestParam("id") Integer documentId,
            @RequestParam(value = "role", defaultValue = "client") String role){
        try {
            return ResponseEntity.ok().body(documentService.downloadDocument(documentId, role));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка" + e);
        }
    }
}

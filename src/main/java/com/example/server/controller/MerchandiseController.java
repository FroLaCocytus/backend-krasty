package com.example.server.controller;
import com.example.server.entity.MerchandiseEntity;
import com.example.server.exception.UniversalException;
import com.example.server.service.MerchandiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchandise")
@CrossOrigin
public class MerchandiseController {

    @Autowired
    private MerchandiseService merchandiseService;

    @PostMapping("/create")
    public ResponseEntity createMerchandise(@RequestBody MerchandiseEntity merchandise){
        try {
            return ResponseEntity.ok().body(merchandiseService.createMerchandise(merchandise));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateMerchandise(@RequestBody MerchandiseEntity request){
        try {
            return ResponseEntity.ok().body(merchandiseService.updateMerchandise(request));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteMerchandise(@RequestParam("id") Integer id){
        try {
            merchandiseService.deleteMerchandise(id);
            return ResponseEntity.ok().build();
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PostMapping("/one")
    public ResponseEntity getOne(@RequestBody MerchandiseEntity request){
        try {
            return ResponseEntity.ok().body(merchandiseService.getOne(request));
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
            @RequestParam(value = "role", defaultValue = "merchandise") String role){
        try {
            return ResponseEntity.ok().body(merchandiseService.getAll(page, size, sort));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }
}

package com.example.server.controller;
import com.example.server.entity.MerchandiseEntity;
import com.example.server.exception.UniversalException;
import com.example.server.service.MerchandiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchandise")
public class MerchandiseController {

    @Autowired
    private MerchandiseService merchandiseService;

    @PostMapping("/create")
    public ResponseEntity createMerchandise(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody MerchandiseEntity merchandise){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(merchandiseService.createMerchandise(token, merchandise));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity updateMerchandise(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody MerchandiseEntity request){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(merchandiseService.updateMerchandise(token, request));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteMerchandise(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestParam("id") Integer id){
        try {
            String token = authorizationHeader.substring(7);
            merchandiseService.deleteMerchandise(token, id);
            return ResponseEntity.ok().build();
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }

    @PostMapping("/one")
    public ResponseEntity getOne(@RequestHeader("Authorization") String authorizationHeader,
                                 @RequestBody MerchandiseEntity request){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(merchandiseService.getOne(token, request));
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
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "role", defaultValue = "merchandise") String role){
        try {
            String token = authorizationHeader.substring(7);
            return ResponseEntity.ok().body(merchandiseService.getAll(token, page, size, sort));
        } catch (UniversalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка:\n" + e);
        }
    }
}

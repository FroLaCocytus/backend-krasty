package com.example.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;
    @Column(nullable = false)
    private Float price;

    @Column(nullable = false, length = 255)
    private String img_path;

    @OneToMany(mappedBy = "productId")
    @JsonIgnore
    private List<BasketProductEntity> manyBasketProduct;

    public ProductEntity() {
    }

    public ProductEntity(String name, String description, Float price, String img_path) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.img_path = img_path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }

    public List<BasketProductEntity> getManyBasketProduct() {
        return manyBasketProduct;
    }

    public void setManyBasketProduct(List<BasketProductEntity> manyBasketProduct) {
        this.manyBasketProduct = manyBasketProduct;
    }
}

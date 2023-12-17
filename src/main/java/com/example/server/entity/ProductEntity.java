package com.example.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String product_name;
    private String product_description;
    private Float price;

    private String img_path;

    @OneToOne(mappedBy = "productId")
    private BasketProductEntity basketProduct;

    public ProductEntity() {
    }

    public ProductEntity(String product_name, String product_description, Float price, String img_path) {
        this.product_name = product_name;
        this.product_description = product_description;
        this.price = price;
        this.img_path = img_path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
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
}

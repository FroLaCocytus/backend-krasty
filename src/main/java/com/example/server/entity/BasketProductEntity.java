package com.example.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "basket_product")
public class BasketProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private ProductEntity productId;

    @ManyToOne
    @JoinColumn(name = "basket_id", nullable = false)
    @JsonIgnore
    private BasketEntity basketId;

    public BasketProductEntity() {
    }

    public BasketProductEntity(Integer count, ProductEntity productId, BasketEntity basketId) {
        this.count = count;
        this.productId = productId;
        this.basketId = basketId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public ProductEntity getProductId() {
        return productId;
    }

    public void setProductId(ProductEntity productId) {
        this.productId = productId;
    }

    public BasketEntity getBasketId() {
        return basketId;
    }

    public void setBasketId(BasketEntity basketId) {
        this.basketId = basketId;
    }


}

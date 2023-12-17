package com.example.server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "basket_product")
public class BasketProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer count;

    @OneToOne
    @JoinColumn(name = "product_id")
    private ProductEntity productId;

    @ManyToOne
    @JoinColumn(name = "basket_id")
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

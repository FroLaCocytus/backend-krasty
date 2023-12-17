package com.example.server.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "baskets")
public class BasketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userId;

    @OneToMany(mappedBy = "basketId")
    private List<BasketProductEntity> manyBasketProduct;

    public BasketEntity() {
    }

    public BasketEntity(UserEntity userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public List<BasketProductEntity> getManyBasketProduct() {
        return manyBasketProduct;
    }

    public void setManyBasketProduct(List<BasketProductEntity> manyBasketProduct) {
        this.manyBasketProduct = manyBasketProduct;
    }
}

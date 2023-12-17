package com.example.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "warehouses")
public class WarehouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String address;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @OneToMany(mappedBy = "warehouseId")
    private List<MerchandiseEntity> manyMerchandise;

    public WarehouseEntity() {
    }

    public WarehouseEntity(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public List<MerchandiseEntity> getManyMerchandise() {
        return manyMerchandise;
    }

    public void setManyMerchandise(List<MerchandiseEntity> manyMerchandise) {
        this.manyMerchandise = manyMerchandise;
    }
}

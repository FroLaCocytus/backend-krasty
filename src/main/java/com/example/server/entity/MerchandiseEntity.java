package com.example.server.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "merchandises")
public class MerchandiseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;
    @Column(nullable = false)
    private Integer count;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonIgnore
    private WarehouseEntity warehouseId;

    public MerchandiseEntity() {
    }

    public MerchandiseEntity(String title, Integer count, WarehouseEntity warehouseId) {
        this.title = title;
        this.count = count;
        this.warehouseId = warehouseId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public WarehouseEntity getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(WarehouseEntity warehouseId) {
        this.warehouseId = warehouseId;
    }
}

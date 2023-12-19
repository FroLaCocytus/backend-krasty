package com.example.server.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true, length = 20)
    private String login;

    @Column(length = 50)
    private String name;
    @Column(length = 255)
    private String email;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(length = 255)
    private String address;

    @Column(length = 12)
    private String phone_number;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity roleId;

    @OneToOne(mappedBy = "userId")
    private BasketEntity basket;

    @OneToOne(mappedBy = "userId")
    private OrderEntity order;

    @OneToMany(mappedBy = "userId")
    private List<WarehouseEntity> manyWarehouse;

    public UserEntity(String login, String password, RoleEntity roleId) {
        this.login = login;
        this.password = password;
        this.roleId = roleId;
    }

    public UserEntity() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public RoleEntity getRoleId() {
        return roleId;
    }

    public void setRoleId(RoleEntity roleId) {
        this.roleId = roleId;
    }

    public BasketEntity getBasket() {
        return basket;
    }

    public void setBasket(BasketEntity basket) {
        this.basket = basket;
    }

    public OrderEntity getOrder() {
        return order;
    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public List<WarehouseEntity> getManyWarehouse() {
        return manyWarehouse;
    }

    public void setManyWarehouse(List<WarehouseEntity> manyWarehouse) {
        this.manyWarehouse = manyWarehouse;
    }
}

package com.example.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 12)
    private String name;

    @OneToMany(mappedBy = "roleId")
    @JsonIgnore
    private List<UserEntity> manyUser;

    @OneToMany(mappedBy = "roleId")
    @JsonIgnore
    private List<RoleDocumentEntity> manyRoleDocument;

    public RoleEntity() {
    }

    public RoleEntity(String name) {
        this.name = name;
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

    public List<UserEntity> getManyUser() {
        return manyUser;
    }

    public void setManyUser(List<UserEntity> manyUser) {
        this.manyUser = manyUser;
    }

    public List<RoleDocumentEntity> getManyRoleDocument() {
        return manyRoleDocument;
    }

    public void setManyRoleDocument(List<RoleDocumentEntity> manyRoleDocument) {
        this.manyRoleDocument = manyRoleDocument;
    }
}

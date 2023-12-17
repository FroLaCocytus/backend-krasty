package com.example.server.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "role_document")
public class RoleDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore
    private RoleEntity roleId;

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    @JsonIgnore
    private DocumentEntity documentId;

    public RoleDocumentEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RoleEntity getRoleId() {
        return roleId;
    }

    public void setRoleId(RoleEntity roleId) {
        this.roleId = roleId;
    }

    public DocumentEntity getDocumentId() {
        return documentId;
    }

    public void setDocumentId(DocumentEntity documentId) {
        this.documentId = documentId;
    }


}

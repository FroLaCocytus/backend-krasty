package com.example.server.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "documents")
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false, unique = true, length = 255)
    private String path;

    @OneToMany(mappedBy = "documentId")
    @JsonIgnore
    private List<RoleDocumentEntity> manyRoleDocument;

    public DocumentEntity() {
    }

    public DocumentEntity(String title, String description, LocalDate date, String path) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.path = path;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<RoleDocumentEntity> getManyRoleDocument() {
        return manyRoleDocument;
    }

    public void setManyRoleDocument(List<RoleDocumentEntity> manyRoleDocument) {
        this.manyRoleDocument = manyRoleDocument;
    }
}

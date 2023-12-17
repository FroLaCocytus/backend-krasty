package com.example.server.repository;

import com.example.server.entity.DocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepo extends JpaRepository<DocumentEntity, Integer> {
    DocumentEntity findByTitle(String documentTitle);
    Page<DocumentEntity> findByIdIn(List<Integer> ids, Pageable pageable);

}

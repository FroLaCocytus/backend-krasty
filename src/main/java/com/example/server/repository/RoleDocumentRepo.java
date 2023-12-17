package com.example.server.repository;

import com.example.server.entity.DocumentEntity;
import com.example.server.entity.RoleDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RoleDocumentRepo extends JpaRepository<RoleDocumentEntity, Integer> {
 @Transactional
 void deleteByDocumentId(DocumentEntity document);
 List<RoleDocumentEntity>  findByDocumentId(DocumentEntity document);
 List<RoleDocumentEntity> findByRoleId_Name(String name);

 Optional<RoleDocumentEntity> findByRoleId_NameAndDocumentId(String roleName, DocumentEntity documentEntity);

}

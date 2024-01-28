package com.example.server.service;


import com.example.server.entity.DocumentEntity;
import com.example.server.entity.RoleDocumentEntity;
import com.example.server.entity.RoleEntity;
import com.example.server.exception.UniversalException;
import com.example.server.repository.*;

import com.example.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DocumentService {
    private static final long FILE_MAX_SIZE = 100 * 1024 * 1024; // 100 МБ
    private final DocumentRepo documentRepo;
    private final RoleRepo roleRepo;
    private final RoleDocumentRepo roleDocumentRepo;
    private final String uploadPath;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public DocumentService(DocumentRepo documentRepo,
                           RoleRepo roleRepo,
                           RoleDocumentRepo roleDocumentRepo,
                           @Value("${document.path}") String uploadPath,
                           JwtTokenProvider jwtTokenProvider) {
        this.documentRepo = documentRepo;
        this.roleRepo = roleRepo;
        this.roleDocumentRepo = roleDocumentRepo;
        this.uploadPath = uploadPath;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    public static List<String> stringToList(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, new TypeReference<List<String>>() {});
    }
    public List<RoleEntity> verifyRolesAndGetRoleEntities(List<String> roles) throws UniversalException {
        List<RoleEntity> roleEntities = new ArrayList<>();
        List<String> notFoundRoles = new ArrayList<>();

        for (String roleName : roles) {
            RoleEntity roleEntity = roleRepo.findByName(roleName);
            if (roleEntity != null) {
                roleEntities.add(roleEntity);
            } else {
                notFoundRoles.add(roleName);
            }
        }

        if (!notFoundRoles.isEmpty()) {
            String notFoundRolesString = String.join(", ", notFoundRoles);
            throw new UniversalException("Роли не найдены: " + notFoundRolesString);
        }

        return roleEntities;
    }

    private void saveRoleDocuments(DocumentEntity document, List<RoleEntity> roles) throws UniversalException {
        for (RoleEntity role : roles) {
            RoleDocumentEntity roleDocument = new RoleDocumentEntity();
            roleDocument.setRoleId(role);
            roleDocument.setDocumentId(document);
            roleDocumentRepo.save(roleDocument); // Сохраняем каждую связь
        }
    }

    public Map<String, Object> createDocument(String token, MultipartFile file, String description, String roles) throws UniversalException, IOException {
        String userRole = jwtTokenProvider.getRoleFromToken(token);
        if (!userRole.equals("accountant")){
            throw new UniversalException("У вас нету доступа к этому действию");
        }
        // Проверяем что все параметры не равны нулю
        if (description.isEmpty() || roles.isEmpty()) {
            throw new UniversalException("Не удалось загрузить документ");
        }
        if (file.isEmpty()) {
            throw new UniversalException("Не удалось загрузить документ: документ пустой");
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        // Проверяем чтобы имя не содержало выход к родительскому каталогу
        if (fileName.contains("..")) {
            throw new UniversalException("Недопустимое имя файла");
        }
        // Проверяем MIME-тип файла
        String mimeType = file.getContentType();
        if (!"application/vnd.ms-excel".equals(mimeType) &&
                !"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimeType) &&
                !"application/msword".equals(mimeType) &&
                !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)) {
            throw new UniversalException("Недопустимый тип файла. Допускаются только документы MS Word и MS Excel.");
        }
        //Получаем дату
        LocalDate currentDate = LocalDate.now(ZoneId.of("Europe/Moscow"));
        // Проверяем размер файла, чтобы он не превышал 100 МБ
        if (file.getSize() > FILE_MAX_SIZE) {
            throw new UniversalException("Размер файла не должен превышать 100 МБ");
        }
        // Проверим что документа с таким именем не существует
        DocumentEntity documentDB = documentRepo.findByTitle(fileName);
        if (documentDB != null){
            throw new UniversalException("Документ с таким именем уже существует");
        }
        // Сохраняем файл на сервер
        Path filePath = Path.of(uploadPath, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        // Сохраняем документ в БД
        DocumentEntity documentEntity = new DocumentEntity(fileName, description, currentDate, "doc/" + fileName);
        documentEntity = documentRepo.save(documentEntity);
        // Получим список ролей
        List<String> listRoles = stringToList(roles);
        List<RoleEntity> listRoleEntity = verifyRolesAndGetRoleEntities(listRoles);
        // Создадим связь между ролями и документом
        saveRoleDocuments(documentEntity,  listRoleEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("title", documentEntity.getTitle());
        response.put("description", documentEntity.getDescription());
        response.put("date", documentEntity.getDate());
        response.put("path", documentEntity.getPath());
        response.put("roles", listRoleEntity);

        return response;
    }

    public Map<String, Object> updateDocument(String token, Integer id, String description, String roles) throws UniversalException, IOException {
        String userRole = jwtTokenProvider.getRoleFromToken(token);
        if (!userRole.equals("accountant")){
            throw new UniversalException("У вас нету доступа к этому действию");
        }
        // Проверяем что все параметры не равны нулю
        if (id == null || description.isEmpty()) {
            throw new UniversalException("Не удалось загрузить документ");
        }

        Optional<DocumentEntity> optionalDocumentEntity = documentRepo.findById(id);
        // Проверим что мы нашли запись документа в БД
        if (optionalDocumentEntity.isEmpty()) {
            throw new UniversalException("Документ не найден");
        }
        System.out.println("1");
        DocumentEntity documentDB = optionalDocumentEntity.get();
        roleDocumentRepo.deleteByDocumentId(documentDB);
        System.out.println("2");

        documentDB.setDescription(description);

        // Получим список ролей
        List<String> listRoles = stringToList(roles);
        List<RoleEntity> listRoleEntity = verifyRolesAndGetRoleEntities(listRoles);
        // Создадим связь между ролями и документом
        saveRoleDocuments(documentDB,  listRoleEntity);

        Map<String, Object> response = new HashMap<>();
        response.put("title", documentDB.getTitle());
        response.put("description", documentDB.getDescription());
        response.put("date", documentDB.getDate());
        response.put("path", documentDB.getPath());
        response.put("roles", listRoleEntity);

        return response;
    }

    public void deleteDocument(String token, Integer id) throws UniversalException{
        String userRole = jwtTokenProvider.getRoleFromToken(token);
        if (!userRole.equals("accountant")){
            throw new UniversalException("У вас нету доступа к этому действию");
        }
        if (id == null) {
            throw new UniversalException("Некорректные данные");
        }
        // Получаем документ из репозитория
        Optional<DocumentEntity> optionalDocumentEntity = documentRepo.findById(id);
        if (optionalDocumentEntity.isEmpty()) {
            throw new UniversalException("Документ не найден");
        }

        DocumentEntity documentDB = optionalDocumentEntity.get();

        // Получаем путь до документа
        Path filePath = Path.of(uploadPath, documentDB.getTitle());

        // Удаляем документ с сервера
        try {
            Files.delete(filePath);
        } catch (Exception e) {
            throw new UniversalException("Не удалось удалить файл: " + e.getMessage());
        }

        // Удаляем все связи ролей с документом
        roleDocumentRepo.deleteByDocumentId(documentDB);

        // Удаляем сам документ
        documentRepo.delete(documentDB);
    }

    public Map<String, Object> getOne(Integer id) throws UniversalException {
        if (id == null) {
            throw new UniversalException("Некорректные данные");
        }
        // Получаем документ из репозитория
        Optional<DocumentEntity> optionalDocumentEntity = documentRepo.findById(id);
        if (optionalDocumentEntity.isEmpty()) {
            throw new UniversalException("Документ не найден");
        }

        DocumentEntity documentDB = optionalDocumentEntity.get();

        // Получаем связанные роли
        List<RoleDocumentEntity> roleDocuments = roleDocumentRepo.findByDocumentId(documentDB);

        List<RoleEntity> roles = roleDocuments.stream()
                .map(RoleDocumentEntity::getRoleId)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("title", documentDB.getTitle());
        response.put("description", documentDB.getDescription());
        response.put("date", documentDB.getDate());
        response.put("path", documentDB.getPath());
        response.put("roles", roles);

        return response;
    }

    public Page<Map<String, Object>> getAll(String token, int page, int size, String sortBy) throws UniversalException {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<DocumentEntity> documentPage;
        String userRole = jwtTokenProvider.getRoleFromToken(token);
        if (userRole.equals("accountant")){
            documentPage = documentRepo.findAll(pageable);
        } else {
            // Если пользователь имеет другую роль, получить только документы этой роли
            List<RoleDocumentEntity> roleDocs = roleDocumentRepo.findByRoleId_Name(userRole);
            List<Integer> documentIds = roleDocs.stream()
                    .map(roleDoc -> roleDoc.getDocumentId().getId())
                    .collect(Collectors.toList());
            documentPage = documentRepo.findByIdIn(documentIds, pageable);
        }

        Page<Map<String, Object>> responsePage = documentPage.map(document -> {
            Map<String, Object> response = new HashMap<>();
            response.put("id", document.getId());
            response.put("title", document.getTitle());
            response.put("description", document.getDescription());
            response.put("date", document.getDate());
            response.put("path", document.getPath());

            if (!userRole.equals("accountant")) {
                // Если роль не бухгалтер, установить только эту роль
                response.put("roles", Collections.singletonList(userRole));
            } else {
                // Если бухгалтер, получить все связанные роли для каждого документа
                List<RoleDocumentEntity> roleDocuments = roleDocumentRepo.findByDocumentId(document);
                List<RoleEntity> roles = roleDocuments.stream()
                        .map(RoleDocumentEntity::getRoleId)
                        .collect(Collectors.toList());
                response.put("roles", roles);
            }

            return response;
        });

        return responsePage;
    }

    @Transactional(readOnly = true)
    public Resource downloadDocument(String token, Integer documentId) throws Exception {
        String userRole = jwtTokenProvider.getRoleFromToken(token);
        Optional<DocumentEntity> documentEntityOptional = documentRepo.findById(documentId);
        if (!documentEntityOptional.isPresent()) {
            throw new UniversalException("Файл не найден с ID " + documentId);
        }
        DocumentEntity documentEntity = documentEntityOptional.get();

        if (!userRole.equals("accountant")){
            // Проверка доступа роли к документу
            Optional<RoleDocumentEntity> roleDocumentOptional = roleDocumentRepo.findByRoleId_NameAndDocumentId(userRole, documentEntity);
            if (!roleDocumentOptional.isPresent()) {
                throw new UniversalException("У вас нету доступа к этому документу");
            }
        }
        Path filePath = Paths.get(uploadPath + "\\" + documentEntity.getTitle()).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists()) {
            return resource;
        } else {
            throw new UniversalException("Файл не найден " + filePath);
        }
    }
}

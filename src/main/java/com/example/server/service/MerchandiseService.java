package com.example.server.service;

import com.example.server.entity.DocumentEntity;
import com.example.server.entity.MerchandiseEntity;
import com.example.server.entity.WarehouseEntity;
import com.example.server.exception.UniversalException;
import com.example.server.repository.MerchandiseRepo;
import com.example.server.repository.WarehouseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MerchandiseService {

    @Autowired
    private MerchandiseRepo merchandiseRepo;

    @Autowired
    private WarehouseRepo warehouseRepo;
    public Map<String, Object> createMerchandise(MerchandiseEntity merchandise) throws UniversalException {

        // Проверяем наличие полей
        if (merchandise == null ||
                merchandise.getTitle() == null ||
                merchandise.getCount() == null) {
            throw new UniversalException("Получены некорректные данные");
        }

        Optional<WarehouseEntity> optionalWarehouseEntity = warehouseRepo.findById(1);
        // Проверим что мы нашли запись склада в БД
        if (optionalWarehouseEntity.isEmpty()) {
            throw new UniversalException("Склад не найден");
        }

        // Создаём запись товара в БД
        WarehouseEntity warehouseDB = optionalWarehouseEntity.get();
        merchandise.setWarehouseId(warehouseDB);
        merchandiseRepo.save(merchandise);

        // Отправляем ответ с сервера
        Map<String, Object> response = new HashMap<>();
        response.put("title", merchandise.getTitle());
        response.put("count", merchandise.getCount());
        return response;
    }

    public Map<String, Object> updateMerchandise(@RequestBody MerchandiseEntity merchandise) throws UniversalException {
        // Проверяем наличие полей
        if (merchandise == null ||
                merchandise.getId() == null ||
                merchandise.getTitle() == null ||
                merchandise.getCount() == null) {
            throw new UniversalException("Получены некорректные данные");
        }

        Optional<MerchandiseEntity> optionalMerchandiseEntity = merchandiseRepo.findById(merchandise.getId());
        // Проверим что мы нашли запись товара в БД
        if (optionalMerchandiseEntity.isEmpty()) {
            throw new UniversalException("Товар не найден");
        }

        // Обновляем запись товара в БД
        MerchandiseEntity merchandiseDB = optionalMerchandiseEntity.get();
        merchandiseDB.setTitle(merchandise.getTitle());
        merchandiseDB.setCount(merchandise.getCount());
        merchandiseRepo.save(merchandiseDB);

        // Отправляем ответ с сервера
        Map<String, Object> response = new HashMap<>();
        response.put("title", merchandiseDB.getTitle());
        return response;
    }

    public void deleteMerchandise(Integer id) throws UniversalException {
        if (id == null) {
            throw new UniversalException("Некорректные данные");
        }

        // Получаем документ из репозитория
        Optional<MerchandiseEntity> optionalMerchandiseEntity = merchandiseRepo.findById(id);
        if (optionalMerchandiseEntity.isEmpty()) {
            throw new UniversalException("Товар не найден");
        }
        MerchandiseEntity merchandiseDB = optionalMerchandiseEntity.get();

        // Удаляем товар из БД
        merchandiseRepo.delete(merchandiseDB);
    }

    public Map<String, Object> getOne(@RequestBody MerchandiseEntity merchandise) throws UniversalException {

        // Проверяем наличие полей
        if (merchandise == null ||
                merchandise.getId() == null) {
            throw new UniversalException("Получены некорректные данные");
        }

        Optional<MerchandiseEntity> optionalMerchandiseEntity = merchandiseRepo.findById(merchandise.getId());
        // Проверим что мы нашли запись товара в БД
        if (optionalMerchandiseEntity.isEmpty()) {
            throw new UniversalException("Товар не найден");
        }

        // Находим запись товара в БД
        MerchandiseEntity merchandiseDB = optionalMerchandiseEntity.get();

        // Отправляем ответ с сервера
        Map<String, Object> response = new HashMap<>();
        response.put("title", merchandiseDB.getTitle());
        response.put("count", merchandiseDB.getCount());
        return response;
    }

    public Page<MerchandiseEntity> getAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        return merchandiseRepo.findAll(pageable);
    }
}

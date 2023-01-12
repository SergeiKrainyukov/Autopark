package com.example.demo3.service;

public interface CRUDService<DTO, Entity> {
    DTO getAllWithDto();

    Entity save(Entity entity);

    Entity update(Entity entity, Long id);

    Entity findById(Long id);

    void deleteById(Long id);
}

package com.example.demo3.repository;

import com.example.demo3.model.entity.ManagerEntity;
import org.springframework.data.repository.CrudRepository;

public interface ManagersRepository extends CrudRepository<ManagerEntity, Long> {}

package com.example.demo3.repository;

import com.example.demo3.model.entity.VehicleEntity;
import org.springframework.data.repository.CrudRepository;

public interface VehiclesRepository extends CrudRepository<VehicleEntity, Long>{}
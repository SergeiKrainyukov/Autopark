package com.example.demo3.repository;

import com.example.demo3.model.entity.DriverEntity;
import org.springframework.data.repository.CrudRepository;

public interface DriversRepository extends CrudRepository<DriverEntity, Long> {
}

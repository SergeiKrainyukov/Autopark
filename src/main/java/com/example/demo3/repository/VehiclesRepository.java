package com.example.demo3.repository;

import com.example.demo3.model.entity.VehicleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface VehiclesRepository extends CrudRepository<VehicleEntity, Long>{
    @Query(value = "SELECT * FROM vehicle_entity WHERE state_number = ?1 LIMIT 1", nativeQuery = true)
    VehicleEntity findVehicleByStateNumber(int stateNumber);
}
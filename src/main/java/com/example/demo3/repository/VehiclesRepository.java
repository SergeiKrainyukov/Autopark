package com.example.demo3.repository;

import com.example.demo3.model.entity.VehicleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VehiclesRepository extends CrudRepository<VehicleEntity, Long>{
    @Query(value = "SELECT * FROM vehicle_entity WHERE state_number = ?1 LIMIT 1", nativeQuery = true)
    VehicleEntity findVehicleByStateNumber(int stateNumber);

    @Query(value = "SELECT * FROM vehicle_entity WHERE enterprise_id = ?1", nativeQuery = true)
    List<VehicleEntity> findAllByEnterpriseId(long enterpriseId);
}
package com.example.demo3.repository;

import com.example.demo3.model.entity.DriverEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DriversRepository extends CrudRepository<DriverEntity, Long> {
    @Query(value = "SELECT * FROM driver_entity WHERE enterprise_id = ?1", nativeQuery = true)
    List<DriverEntity> getAllByEnterpriseId(long enterpriseId);
}

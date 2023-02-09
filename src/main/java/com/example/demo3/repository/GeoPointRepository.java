package com.example.demo3.repository;

import com.example.demo3.model.entity.GeoPointEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeoPointRepository extends CrudRepository<GeoPointEntity, Long> {
    @Query(value = "SELECT * FROM geo_point_entity WHERE date BETWEEN ?1 AND ?2", nativeQuery = true)
    List<GeoPointEntity> findAllBetweenDates(long startDate, long endDate);

    @Query(value = "SELECT * FROM geo_point_entity WHERE date BETWEEN ?1 AND ?2 AND vehicle_id = ?3 ORDER BY date ASC", nativeQuery = true)
    List<GeoPointEntity> findAllByVehicleIdBetweenDates(long startDate, long endDate, long vehicleId);

    @Query(value = "SELECT * FROM geo_point_entity WHERE vehicle_id = ?1 ORDER BY date ASC", nativeQuery = true)
    List<GeoPointEntity> findAllByVehicleId(long vehicleId);
}

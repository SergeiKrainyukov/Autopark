package com.example.demo3.repository;

import com.example.demo3.model.entity.TripEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TripRepository extends CrudRepository<TripEntity, Long> {

    @Query(value = "SELECT * FROM trip_entity WHERE vehicle_id = ?1 AND start_date >= ?2 AND end_date <= ?3", nativeQuery = true)
    List<TripEntity> getAllByVehicleIdAndDates(long vehicleId, long startDate, long endDate);

    @Query(value = "SELECT * FROM trip_entity WHERE vehicle_id = ?1", nativeQuery = true)
    List<TripEntity> getAllByVehicleId(long vehicleId);

    @Query(value = "SELECT * FROM trip_entity WHERE start_date = ?1 LIMIT 1", nativeQuery = true)
    TripEntity getByStartDate(long startDate);
}

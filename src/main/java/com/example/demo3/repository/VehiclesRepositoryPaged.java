package com.example.demo3.repository;

import com.example.demo3.model.entity.VehicleEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface VehiclesRepositoryPaged extends PagingAndSortingRepository<VehicleEntity, Long> {
}

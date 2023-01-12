package com.example.demo3.repository;

import com.example.demo3.model.entity.BrandEntity;
import org.springframework.data.repository.CrudRepository;

public interface BrandsRepository extends CrudRepository<BrandEntity, Long> {
}

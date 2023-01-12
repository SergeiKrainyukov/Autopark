package com.example.demo3.repository;

import com.example.demo3.model.entity.EnterpriseEntity;
import org.springframework.data.repository.CrudRepository;

public interface EnterprisesRepository extends CrudRepository<EnterpriseEntity, Long> { }

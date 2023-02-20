package com.example.demo3.model.dto;

import com.example.demo3.model.entity.EnterpriseEntity;

import java.util.ArrayList;
import java.util.List;

public class EnterprisesDto {
    private List<EnterpriseEntity> enterprises;

    public List<EnterpriseEntity> getEnterprises() {
        if (enterprises == null) {
            enterprises = new ArrayList<>();
        }
        return enterprises;
    }

    public void addEnterprise(EnterpriseEntity enterpriseEntity) {
        if (enterprises == null) {
            enterprises = new ArrayList<>();
        }
        enterprises.add(enterpriseEntity);
    }
}

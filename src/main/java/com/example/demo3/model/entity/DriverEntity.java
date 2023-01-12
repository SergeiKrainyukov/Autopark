package com.example.demo3.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DriverEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long enterpriseId;
    private Long vehicleId;
    private boolean isActive;
    private String name;
    private Integer salary;

    public DriverEntity() {
    }

    public DriverEntity(String name, Integer salary, Long enterpriseId) {
        this.name = name;
        this.salary = salary;
        this.enterpriseId = enterpriseId;
        this.isActive = false;
    }

    public DriverEntity(String name, Integer salary, Long enterpriseId, Long vehicleId, boolean isActive) {
        this.name = name;
        this.salary = salary;
        this.enterpriseId = enterpriseId;
        this.vehicleId = vehicleId;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

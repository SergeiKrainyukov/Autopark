package com.example.demo3.model.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class VehicleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer stateNumber;

    private Long brandId;

    private Long enterpriseId;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> drivers;

    private Integer price;

    private Integer year;

    private Integer mileage;

    private Long purchaseDate;

    public VehicleEntity() {
    }

    public VehicleEntity(Integer stateNumber, Integer price, Integer year, Integer mileage, Long enterpriseId, Long brandId, Long purchaseDate) {
        this.stateNumber = stateNumber;
        this.price = price;
        this.year = year;
        this.mileage = mileage;
        this.enterpriseId = enterpriseId;
        this.brandId = brandId;
        this.purchaseDate = purchaseDate;
        drivers = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public List<Long> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Long> drivers) {
        this.drivers = drivers;
    }

    public Integer getStateNumber() {
        return stateNumber;
    }

    public void setStateNumber(Integer stateNumber) {
        this.stateNumber = stateNumber;
    }

    public Long getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}

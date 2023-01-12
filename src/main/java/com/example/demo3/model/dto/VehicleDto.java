package com.example.demo3.model.dto;

import com.example.demo3.model.entity.VehicleEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class VehicleDto {
    private Long id;

    private Long brandId;

    private Long enterpriseId;

    private List<Long> drivers;

    private Integer price;

    private Integer year;

    private Integer mileage;

    private String purchaseDateZoned;

    private String purchaseDateUTC;

    public static VehicleDto fromVehicleEntity(VehicleEntity vehicleEntity, String timeZone) {
        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(vehicleEntity.getId());
        vehicleDto.setBrandId(vehicleEntity.getBrandId());
        vehicleDto.setEnterpriseId(vehicleEntity.getEnterpriseId());
        vehicleDto.setDrivers(vehicleEntity.getDrivers());
        vehicleDto.setPrice(vehicleEntity.getPrice());
        vehicleDto.setYear(vehicleEntity.getYear());
        vehicleDto.setMileage(vehicleEntity.getMileage());

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(vehicleEntity.getPurchaseDate());
        DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        vehicleDto.purchaseDateUTC = timeFormat.format(calendar.getTimeInMillis());
        if (timeZone != null)
            timeFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        vehicleDto.purchaseDateZoned = timeFormat.format(calendar.getTimeInMillis());

        return vehicleDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPurchaseDateZoned() {
        return purchaseDateZoned;
    }

    public String getPurchaseDateUTC() {
        return purchaseDateUTC;
    }
}

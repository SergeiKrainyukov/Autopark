package com.example.demo3.model.dto;

import com.example.demo3.model.entity.DriverEntity;

import java.util.ArrayList;
import java.util.List;

public class DriversDto {

    private List<DriverEntity> driverEntities;

    public List<DriverEntity> getDrivers() {
        if (driverEntities == null) {
            driverEntities = new ArrayList<>();
        }
        return driverEntities;
    }
}

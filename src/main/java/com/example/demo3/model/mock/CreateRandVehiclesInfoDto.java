package com.example.demo3.model.mock;

import java.util.List;

public class CreateRandVehiclesInfoDto {
    private final Long managerId;
    private final List<Long> enterprises;
    private final int vehiclesCount;
    private final int driversCount;

    public CreateRandVehiclesInfoDto(Long managerId, List<Long> enterprises, int vehiclesCount, int driversCount) {
        this.managerId = managerId;
        this.enterprises = enterprises;
        this.vehiclesCount = vehiclesCount;
        this.driversCount = driversCount;
    }

    public List<Long> getEnterprises() {
        return enterprises;
    }

    public int getVehiclesCount() {
        return vehiclesCount;
    }

    public int getDriversCount() {
        return driversCount;
    }

    public Long getManagerId() {
        return managerId;
    }
}

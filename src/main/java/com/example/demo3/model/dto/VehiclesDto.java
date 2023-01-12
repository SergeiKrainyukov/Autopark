package com.example.demo3.model.dto;

import java.util.ArrayList;
import java.util.List;

public class VehiclesDto {

    private List<VehicleDto> vehicles;

    public List<VehicleDto> getVehicles() {
        if (vehicles == null) {
            vehicles = new ArrayList<>();
        }
        return vehicles;
    }

    public void setVehicles(List<VehicleDto> vehicleDtoList) {
        this.vehicles = vehicleDtoList;
    }
}


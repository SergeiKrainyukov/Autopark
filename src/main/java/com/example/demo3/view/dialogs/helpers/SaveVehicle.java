package com.example.demo3.view.dialogs.helpers;

import com.example.demo3.model.entity.VehicleEntity;

@FunctionalInterface
public interface SaveVehicle {
    void save(VehicleEntity vehicleEntity);
}

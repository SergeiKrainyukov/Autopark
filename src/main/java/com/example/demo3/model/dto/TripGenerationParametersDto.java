package com.example.demo3.model.dto;

public class TripGenerationParametersDto {
    private final long vehicleId;
    private final int distance;
    private final int maxSpeed;
    private final int velocity;

    public TripGenerationParametersDto(long vehicleId, int distance, int maxSpeed, int velocity) {
        this.vehicleId = vehicleId;
        this.distance = distance;
        this.maxSpeed = maxSpeed;
        this.velocity = velocity;
    }

    public int getDistance() {
        return distance;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getVelocity() {
        return velocity;
    }

    public long getVehicleId() {
        return vehicleId;
    }
}

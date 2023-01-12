package com.example.demo3.model.dto;

import java.util.ArrayList;
import java.util.List;

public class TripsDto {
    private final List<TripDto> trips;

    public TripsDto() {
        trips = new ArrayList<>();
    }

    public TripsDto(List<TripDto> trips) {
        this.trips = trips;
    }

    public List<TripDto> getTrips() {
        if (trips == null) {
            return new ArrayList<>();
        }
        return trips;
    }
}

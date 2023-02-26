package com.example.demo3.view.dialogs.helpers;

import com.example.demo3.model.dto.TripDto;
import com.example.demo3.model.entity.TripEntity;

import java.util.List;

@FunctionalInterface
public interface GetTripsHelper {
    public List<TripDto> getTrips(long vehicleId, long startDate, long endDate);
}

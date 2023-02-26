package com.example.demo3.view.dialogs.helpers;

import com.example.demo3.model.dto.TripDto;

import java.util.List;

@FunctionalInterface
public interface GetTripsDtoHelper {
    List<TripDto> getTrips(long vehicleId, long startDate, long endDate);
}

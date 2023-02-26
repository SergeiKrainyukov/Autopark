package com.example.demo3.view.dialogs.helpers;

import com.example.demo3.model.entity.TripEntity;

import java.util.List;

public interface GetTripEntitiesHelper {
    List<TripEntity> getTripEntities(long vehicleId, long dateFrom, long dateTo);
}

package com.example.demo3.view.dialogs.helpers;

import com.example.demo3.model.entity.GeoPointEntity;

import java.util.List;

@FunctionalInterface
public interface GetGeoPointsHelper {
    List<GeoPointEntity> getGeoPointEntities(long vehicleId, long startDate, long endDate);
}

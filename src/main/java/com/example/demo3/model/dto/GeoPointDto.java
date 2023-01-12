package com.example.demo3.model.dto;

import com.example.demo3.model.entity.GeoPointEntity;
import com.google.appengine.api.search.GeoPoint;

public class GeoPointDto {
    private Long id;
    private Long vehicleId;
    private GeoPoint geoPoint;
    private String date;

    public GeoPointDto(Long id, Long vehicleId, GeoPoint geoPoint, String date) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.geoPoint = geoPoint;
        this.date = date;
    }

    public static GeoPointDto fromGeoPointEntity(GeoPointEntity geoPointEntity, String date) {
        return new GeoPointDto(
                geoPointEntity.getId(),
                geoPointEntity.getVehicleId(),
                new GeoPoint(geoPointEntity.getGeoPoint().getX(), geoPointEntity.getGeoPoint().getY()),
                date
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

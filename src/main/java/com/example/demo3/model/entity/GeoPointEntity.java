package com.example.demo3.model.entity;

import com.google.appengine.api.search.GeoPoint;
import org.springframework.data.geo.Point;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GeoPointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long vehicleId;
    private Point geoPoint;
    private Long date;

    public GeoPointEntity() {
    }

    public GeoPointEntity(Long vehicleId, GeoPoint geoPoint, Long date) {
        this.vehicleId = vehicleId;
        this.geoPoint = new Point(geoPoint.getLatitude(), geoPoint.getLongitude());
        this.date = date;
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

    public Point getGeoPoint() {
        return geoPoint;
    }

    public void setPoint(Point geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}

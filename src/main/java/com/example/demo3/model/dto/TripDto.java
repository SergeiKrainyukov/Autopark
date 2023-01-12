package com.example.demo3.model.dto;

import com.google.appengine.api.search.GeoPoint;

public class TripDto {
    private String startDate;
    private String endDate;
    private Place startPlace;
    private Place endPlace;

    public TripDto() {
    }

    public TripDto(String startDate, String endDate, GeoPoint startPoint, String startPlaceName, GeoPoint endPoint, String endPlaceName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startPlace = new Place(startPoint, startPlaceName);
        this.endPlace = new Place(endPoint, endPlaceName);
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Place getStartPlace() {
        return startPlace;
    }

    public Place getEndPlace() {
        return endPlace;
    }

    public static class Place {
        private final GeoPoint geoPoint;
        private final String placeName;

        public Place(GeoPoint geoPoint, String placeName) {
            this.geoPoint = geoPoint;
            this.placeName = placeName;
        }

        public GeoPoint getGeoPoint() {
            return geoPoint;
        }

        public String getPlaceName() {
            return placeName;
        }
    }
}

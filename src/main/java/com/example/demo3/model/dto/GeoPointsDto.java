package com.example.demo3.model.dto;

import java.util.ArrayList;
import java.util.List;

public class GeoPointsDto {
    List<GeoPointDto> geoPointEntities;

    public List<GeoPointDto> getGeoPoints(){
        if (geoPointEntities == null){
            return new ArrayList<>();
        }
        return geoPointEntities;
    }

    public void setGeoPoints(List<GeoPointDto> geoPoints){
        this.geoPointEntities = geoPoints;
    }
}

package com.example.demo3;

import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.service.TripService;
import com.google.appengine.api.search.GeoPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TripServiceTest {

    private final TripService tripService = new TripService();

    @Test
    void getTripAsJsonTest() throws Exception {
        List<TripEntity> tripEntities = new ArrayList<>();
        tripEntities.add(new TripEntity(1L, 0L, 100L, 10));

        List<GeoPointEntity> geoPointEntities = new ArrayList<>();
        geoPointEntities.add(new GeoPointEntity(1L, new GeoPoint(55.0, 120.0), 50L));

        JSONObject jsonObject = tripService.getTripAsJSON(tripEntities, geoPointEntities);
        JSONArray jsonArray = (JSONArray) jsonObject.get("features");
        Assertions.assertTrue(jsonArray.length() > 0);
    }
}

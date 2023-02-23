package com.example.demo3.common;

import com.example.demo3.model.entity.GeoPointEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class GeoJSONHelper {

    public static JSONObject getGeoJson(List<GeoPointEntity> geoPointEntities) {

        JSONObject featureCollection = new JSONObject();
        featureCollection.put("type", "FeatureCollection");
        JSONObject properties = new JSONObject();
        properties.put("name", "ESPG:4326");
        JSONObject crs = new JSONObject();
        crs.put("type", "name");
        crs.put("properties", properties);
        featureCollection.put("crs", crs);

        JSONArray features = new JSONArray();

        for (GeoPointEntity geoPointEntity : geoPointEntities) {
            JSONObject feature = new JSONObject();
            feature.put("type", "Feature");
            JSONObject geometry = new JSONObject();
            JSONObject desc = new JSONObject();

            JSONArray JSONArrayCoord = new JSONArray("[" + geoPointEntity.getGeoPoint().getX() + "," + geoPointEntity.getGeoPoint().getY() + "]");

            geometry.put("type", "Point");
            geometry.put("coordinates", JSONArrayCoord);
            feature.put("geometry", geometry);
            feature.put("properties", desc);
            desc.put("name", "Oregon");

            features.put(feature);
            featureCollection.put("features", features);
        }
        return featureCollection;
    }
}

package com.example.demo3.service;

import com.example.demo3.model.dto.TripDto;
import com.example.demo3.model.dto.TripsDto;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.TripEntity;
import com.google.appengine.api.search.GeoPoint;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@PropertySource("classpath:application.properties")
@SpringComponent
public class TripService {

    private static final String dateFormatPattern = "dd.MM.yyyy HH:mm:ss";

    @Value("${map_box_api_key}")
    private String API_KEY;
    @Value("${map_box_url}")
    private String URL;
    @Value("${access_token_parameter}")
    private String ACCESS_TOKEN_PARAMETER;

    private static final char COMMA_SEPARATOR = ',';

    public JSONObject getTripAsJSON(List<TripEntity> tripEntities, List<GeoPointEntity> geoPointEntities) {
        try {
            if (tripEntities.size() == 0) return new JSONObject();

            long minStartDate = tripEntities.get(0).getStartDate();
            long maxEndDate = tripEntities.get(0).getEndDate();

            for (TripEntity tripEntity : tripEntities) {
                if (tripEntity.getStartDate() < minStartDate) {
                    minStartDate = tripEntity.getStartDate();
                }
                if (tripEntity.getEndDate() > maxEndDate) {
                    maxEndDate = tripEntity.getEndDate();
                }
            }
            return getGeoJson(filterGeopointsByDates(geoPointEntities, minStartDate, maxEndDate));

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private List<GeoPointEntity> filterGeopointsByDates(List<GeoPointEntity> geoPointEntities, long startDate, long endDate) {
        return geoPointEntities.stream()
                .filter(geoPointEntity -> geoPointEntity.getDate() >= startDate && geoPointEntity.getDate() <= endDate)
                .collect(Collectors.toList());
    }

    public TripsDto getAllTripsByVehicleIdAndDates(TimeZone enterpriseTimeZone, List<TripEntity> tripEntitiesByVehicleIdBetweenDates, List<GeoPointEntity> allGeoPointsByVehicleIdAndDates) {
        try {
            if (tripEntitiesByVehicleIdBetweenDates.size() == 0) return new TripsDto();
            long vehicleId = tripEntitiesByVehicleIdBetweenDates.get(0).getVehicleId();
            List<TripDto> tripDtoList = new ArrayList<>();
            for (TripEntity tripEntity : tripEntitiesByVehicleIdBetweenDates) {
                List<GeoPointEntity> geoPointEntities = allGeoPointsByVehicleIdAndDates.stream().filter(geoPointEntity -> geoPointEntity.getVehicleId() == vehicleId && geoPointEntity.getDate() >= tripEntity.getStartDate() && geoPointEntity.getDate() <= tripEntity.getEndDate()).collect(Collectors.toList());
                if (geoPointEntities.size() == 0) continue;

                GeoPointEntity firstGeopointEntity = geoPointEntities.get(0);
                GeoPointEntity lastGeopointEntity = geoPointEntities.get(geoPointEntities.size() - 1);

                GeoPoint startGeoPoint = new GeoPoint(firstGeopointEntity.getGeoPoint().getX(), firstGeopointEntity.getGeoPoint().getY());
                GeoPoint endGeoPoint = new GeoPoint(lastGeopointEntity.getGeoPoint().getX(), lastGeopointEntity.getGeoPoint().getY());

                String startDateFormatted = getZonedTimeStringFormatted(enterpriseTimeZone, tripEntity.getStartDate());
                String endDateFormatted = getZonedTimeStringFormatted(enterpriseTimeZone, tripEntity.getEndDate());

                String startPlaceName = requestPlace(URL + startGeoPoint.getLatitude() + COMMA_SEPARATOR + startGeoPoint.getLongitude() + ACCESS_TOKEN_PARAMETER + API_KEY);
                String endPlaceName = requestPlace(URL + endGeoPoint.getLatitude() + COMMA_SEPARATOR + endGeoPoint.getLongitude() + ACCESS_TOKEN_PARAMETER + API_KEY);

                tripDtoList.add(new TripDto(startDateFormatted, endDateFormatted, startGeoPoint, startPlaceName, endGeoPoint, endPlaceName));
            }
            return new TripsDto(tripDtoList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private JSONObject getGeoJson(List<GeoPointEntity> geoPointEntities) {
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

    private String getZonedTimeStringFormatted(TimeZone timezone, Long dateMillis) {
        Calendar calendar = Calendar.getInstance(timezone);
        calendar.setTime(new Date(dateMillis));
        DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        dateFormat.setTimeZone(timezone);
        return dateFormat.format(calendar.getTime());
    }

    public String requestPlace(String url) {
        String placeName = "";
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
                JSONParser parser = new JSONParser();
                Object resultObject = parser.parse(json);
                com.nimbusds.jose.shaded.json.JSONObject obj = (com.nimbusds.jose.shaded.json.JSONObject) resultObject;
                com.nimbusds.jose.shaded.json.JSONArray features = (com.nimbusds.jose.shaded.json.JSONArray) obj.get("features");
                com.nimbusds.jose.shaded.json.JSONObject place = (com.nimbusds.jose.shaded.json.JSONObject) features.get(0);
                placeName = place.get("place_name").toString();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return placeName;
    }
}

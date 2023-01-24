package com.example.demo3.common.utilities;

import com.example.demo3.model.dto.TripGenerationParametersDto;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.repository.GeoPointRepository;
import com.example.demo3.repository.TripRepository;
import com.google.appengine.api.search.GeoPoint;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Configuration
@PropertySource("classpath:application.properties")
public class TripOnMapGenerationUtility {

    private final GeoPointRepository geoPointRepository;
    private final TripRepository tripRepository;

    @Value("${open_route_url}")
    private String BASE_URL;

    private static final double FIRST_GEOPOINT_LATITUDE = 8.681495;
    private static final double FIRST_GEOPOINT_LONGITUDE = 49.41461;
    private static final double LAST_GEOPOINT_LATITUDE = 8.687872;
    private static final double LAST_GEOPOINT_LONGITUDE = 49.420318;

    private static final String CONTENT_TYPE_HEADER = "content-type";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private static final String UTF_8 = "UTF-8";
    private static final String FEATURES = "features";
    private static final String GEOMETRY = "geometry";
    private static final String COORDINATES = "coordinates";
    private static final String START = "&start=";
    private static final String END = "&end=";
    private static final char COMMA_SEPARATOR = ',';

    @Autowired
    public TripOnMapGenerationUtility(GeoPointRepository geoPointRepository, TripRepository tripRepository) {
        this.geoPointRepository = geoPointRepository;
        this.tripRepository = tripRepository;
    }

    public void startGeneratingRoute(TripGenerationParametersDto tripGenerationParametersDto) {
        if (tripGenerationParametersDto == null) return;
        new Thread(() -> {
            long startTripDate = Instant.now().toEpochMilli();
            List<GeoPoint> geoPoints = generateGeoPoints();
            TripEntity tripEntity = tripRepository.save(new TripEntity(tripGenerationParametersDto.getVehicleId(), startTripDate, startTripDate, tripGenerationParametersDto.getDistance()));
            for (GeoPoint geoPoint : geoPoints) {
                long currentDateMillis = Instant.now().toEpochMilli();
                geoPointRepository.save(new GeoPointEntity(tripGenerationParametersDto.getVehicleId(), geoPoint, currentDateMillis));
                tripEntity.setEndDate(currentDateMillis);
                tripRepository.save(tripEntity);
                sleepCurrentThread();
            }
        }).start();
    }

    public void sleepCurrentThread() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GeoPoint> generateGeoPoints() {
        List<GeoPoint> geoPoints = new ArrayList<>();
        Random r = new Random();
        float randomLatFirst = r.nextFloat() * 0.2f;
        float randomLongFirst = r.nextFloat() * 0.2f;
        float randomLatSecond = r.nextFloat() * 0.2f;
        float randomLongSecond = r.nextFloat() * 0.2f;
        GeoPoint startGeoPoint = new GeoPoint(FIRST_GEOPOINT_LATITUDE + randomLatFirst, FIRST_GEOPOINT_LONGITUDE + randomLongFirst);
        GeoPoint endGeoPoint = new GeoPoint(LAST_GEOPOINT_LATITUDE + randomLatSecond, LAST_GEOPOINT_LONGITUDE + randomLongSecond);

        String URL = BASE_URL + START + startGeoPoint.getLatitude() + COMMA_SEPARATOR + startGeoPoint.getLongitude() + END + endGeoPoint.getLatitude() + COMMA_SEPARATOR + endGeoPoint.getLongitude();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(URL);
            request.addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE);
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), UTF_8);
            try {
                JSONParser parser = new JSONParser();
                Object resultObject = parser.parse(json);
                com.nimbusds.jose.shaded.json.JSONObject obj = (com.nimbusds.jose.shaded.json.JSONObject) resultObject;
                com.nimbusds.jose.shaded.json.JSONArray features = (com.nimbusds.jose.shaded.json.JSONArray) obj.get(FEATURES);
                com.nimbusds.jose.shaded.json.JSONObject place = (com.nimbusds.jose.shaded.json.JSONObject) features.get(0);
                com.nimbusds.jose.shaded.json.JSONObject geometry = (com.nimbusds.jose.shaded.json.JSONObject) place.get(GEOMETRY);
                com.nimbusds.jose.shaded.json.JSONArray coordinates = (com.nimbusds.jose.shaded.json.JSONArray) geometry.get(COORDINATES);
                for (Object coordinate : coordinates) {
                    com.nimbusds.jose.shaded.json.JSONArray coordinatesArray = (com.nimbusds.jose.shaded.json.JSONArray) coordinate;
                    double latitude = (double) coordinatesArray.get(0);
                    double longitude = (double) coordinatesArray.get(1);
                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                    geoPoints.add(geoPoint);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return geoPoints;
    }
}

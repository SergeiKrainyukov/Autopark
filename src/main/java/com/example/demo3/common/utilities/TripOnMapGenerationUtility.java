package com.example.demo3.common.utilities;

import com.example.demo3.model.dto.TripGenerationParametersDto;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.repository.GeoPointRepository;
import com.example.demo3.repository.TripRepository;
import com.google.appengine.api.search.GeoPoint;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringComponent
public class TripOnMapGenerationUtility {

    private final Thread thread;
    private TripGenerationParametersDto tripGenerationParametersDto;

    private static final String API_KEY = "5b3ce3597851110001cf6248c88c5b7c7559427cb92111cedc57c866";
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=";

    @Autowired
    public TripOnMapGenerationUtility(GeoPointRepository geoPointRepository, TripRepository tripRepository) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (tripGenerationParametersDto == null)
                    thread.interrupt();
                Date startTripDate = new Date();
                List<GeoPoint> geoPoints = generateGeoPointsV2();
                tripRepository.save(new TripEntity(tripGenerationParametersDto.getVehicleId(), startTripDate.getTime(), startTripDate.getTime()));
                System.out.println("Trip generation started");
                for (GeoPoint geoPoint : geoPoints) {
                    try {
                        System.out.println("Generation is working");
                        TripEntity tripEntity = tripRepository.getByStartDate(startTripDate.getTime());
                        geoPointRepository.save(new GeoPointEntity(tripGenerationParametersDto.getVehicleId(), geoPoint, new Date().getTime()));
                        tripEntity.setEndDate(new Date().getTime());
                        tripRepository.save(tripEntity);
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Trip generation stopped");
            }
        });
    }

    public void startGeneratingRoute(TripGenerationParametersDto tripGenerationParametersDto) {
        this.tripGenerationParametersDto = tripGenerationParametersDto;
        if (thread.isAlive()) thread.interrupt();
        thread.start();
    }

    public void stopGeneratingRoute() {
        thread.interrupt();
    }

    //TODO: Randomize start end geopoints
    private List<GeoPoint> generateGeoPointsV2() {
        List<GeoPoint> geoPoints = new ArrayList<>();
        GeoPoint startGeoPoint = new GeoPoint(8.681495, 49.41461);
        GeoPoint endGeoPoint = new GeoPoint(8.687872, 49.420318);

        String URL = BASE_URL + API_KEY + "&start=" + startGeoPoint.getLatitude() + "," + startGeoPoint.getLongitude() + "&end=" + endGeoPoint.getLatitude() + "," + endGeoPoint.getLongitude();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(URL);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            try {
                JSONParser parser = new JSONParser();
                Object resultObject = parser.parse(json);
                com.nimbusds.jose.shaded.json.JSONObject obj = (com.nimbusds.jose.shaded.json.JSONObject) resultObject;
                com.nimbusds.jose.shaded.json.JSONArray features = (com.nimbusds.jose.shaded.json.JSONArray) obj.get("features");
                com.nimbusds.jose.shaded.json.JSONObject place = (com.nimbusds.jose.shaded.json.JSONObject) features.get(0);
                com.nimbusds.jose.shaded.json.JSONObject geometry = (com.nimbusds.jose.shaded.json.JSONObject) place.get("geometry");
                com.nimbusds.jose.shaded.json.JSONArray coordinates = (com.nimbusds.jose.shaded.json.JSONArray) geometry.get("coordinates");
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

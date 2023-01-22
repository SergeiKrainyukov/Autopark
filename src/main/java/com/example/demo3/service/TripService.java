package com.example.demo3.service;

import com.example.demo3.model.dto.TripDto;
import com.example.demo3.model.dto.TripsDto;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.EnterprisesRepository;
import com.example.demo3.repository.GeoPointRepository;
import com.example.demo3.repository.TripRepository;
import com.example.demo3.repository.VehiclesRepository;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringComponent
public class TripService {

    private final TripRepository tripRepository;
    private final GeoPointRepository geoPointRepository;
    private final EnterprisesRepository enterprisesRepository;
    private final VehiclesRepository vehiclesRepository;

    private static final String dateFormatPattern = "dd.MM.yyyy HH:mm:ss";

    private static final String API_KEY = "pk.eyJ1Ijoic2VyZ2Vpa3JhaSIsImEiOiJjbGJuaDgxOHAwYTcxM29sOGtra3owdWplIn0.JlUr7-JutfgiO0vadGOHRQ";
    private static final String URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/";
    private static final String ACCESS_TOKEN_PARAMETER = ".json?access_token=";
    private static final String UTC_TIMEZONE = "UTC";
    private static final char COMMA_SEPARATOR = ',';

    @Autowired
    public TripService(TripRepository tripRepository, GeoPointRepository geoPointRepository, EnterprisesRepository enterprisesRepository, VehiclesRepository vehiclesRepository) {
        this.tripRepository = tripRepository;
        this.geoPointRepository = geoPointRepository;
        this.enterprisesRepository = enterprisesRepository;
        this.vehiclesRepository = vehiclesRepository;
    }

    public JSONObject getTrip(long vehicleId, String dateFrom, String dateTo) {
        try {
            long startDate = getLongDate(dateFrom);
            long endDate = getLongDate(dateTo);

            List<TripEntity> tripEntities = tripRepository.getAllByVehicleIdAndDates(vehicleId, startDate, endDate);

            if (tripEntities.size() == 0) return null;

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
            List<GeoPointEntity> geoPointEntities = new ArrayList<>(geoPointRepository.findAllBetweenDates(minStartDate, maxEndDate));
            return getGeoJson(geoPointEntities);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public TripsDto getAllTripsByVehicleIdAndDates(long vehicleId, String dateFrom, String dateTo) {
        try {
            VehicleEntity vehicleEntity = vehiclesRepository.findById(vehicleId).orElse(null);
            if (vehicleEntity == null) return null;
            EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
            if (enterpriseEntity == null) return null;

            long startDate = getLongDate(dateFrom);
            long endDate = getLongDate(dateTo);
            List<TripEntity> tripEntities = tripRepository.getAllByVehicleIdAndDates(vehicleId, startDate, endDate);
            if (tripEntities.size() == 0) return null;
            List<TripDto> tripDtoList = new ArrayList<>();
            for (TripEntity tripEntity : tripEntities) {
                List<GeoPointEntity> geoPointEntities = new ArrayList<>(geoPointRepository.findAllBetweenDates(tripEntity.getStartDate(), tripEntity.getEndDate()));
                if (geoPointEntities.size() == 0) continue;
                geoPointEntities.sort((o1, o2) -> {
                    if (o1.getDate() < o2.getDate()) return 1;
                    else if (o1.getDate().equals(o2.getDate())) return 0;
                    else return -1;
                });
                GeoPointEntity firstGeopointEntity = geoPointEntities.get(0);
                GeoPointEntity lastGeopointEntity = geoPointEntities.get(geoPointEntities.size() - 1);

                GeoPoint startGeoPoint = new GeoPoint(firstGeopointEntity.getGeoPoint().getX(), firstGeopointEntity.getGeoPoint().getY());
                GeoPoint endGeoPoint = new GeoPoint(lastGeopointEntity.getGeoPoint().getX(), lastGeopointEntity.getGeoPoint().getY());

                String startDateFormatted = getZonedTimeStringFormatted(TimeZone.getTimeZone(enterpriseEntity.getTimeZone()), tripEntity.getStartDate());
                String endDateFormatted = getZonedTimeStringFormatted(TimeZone.getTimeZone(enterpriseEntity.getTimeZone()), tripEntity.getEndDate());

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

    public TripsDto getAllTripsByVehicleIdForUI(long vehicleId) {
        try {
            VehicleEntity vehicleEntity = vehiclesRepository.findById(vehicleId).orElse(null);
            if (vehicleEntity == null) return new TripsDto();
            EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
            if (enterpriseEntity == null) return new TripsDto();
            List<TripEntity> tripEntities = tripRepository.getAllByVehicleId(vehicleId);
            if (tripEntities.size() == 0) return new TripsDto();
            List<TripDto> tripDtoList = new ArrayList<>();
            for (TripEntity tripEntity : tripEntities) {
                List<GeoPointEntity> geoPointEntities = new ArrayList<>(geoPointRepository.findAllByVehicleIdBetweenDates(tripEntity.getStartDate(), tripEntity.getEndDate(), vehicleId));
                if (geoPointEntities.size() == 0) continue;

                GeoPointEntity firstGeopointEntity = geoPointEntities.get(0);
                GeoPointEntity lastGeopointEntity = geoPointEntities.get(geoPointEntities.size() - 1);

                GeoPoint startGeoPoint = new GeoPoint(firstGeopointEntity.getGeoPoint().getX(), firstGeopointEntity.getGeoPoint().getY());
                GeoPoint endGeoPoint = new GeoPoint(lastGeopointEntity.getGeoPoint().getX(), lastGeopointEntity.getGeoPoint().getY());

                String startDateFormatted = getZonedTimeStringFormatted(TimeZone.getDefault(), tripEntity.getStartDate());
                String endDateFormatted = getZonedTimeStringFormatted(TimeZone.getDefault(), tripEntity.getEndDate());

                String startPlaceName = requestPlace(URL + startGeoPoint.getLatitude() + COMMA_SEPARATOR + startGeoPoint.getLongitude() + ACCESS_TOKEN_PARAMETER + API_KEY);
                String endPlaceName = requestPlace(URL + endGeoPoint.getLatitude() + COMMA_SEPARATOR + endGeoPoint.getLongitude() + ACCESS_TOKEN_PARAMETER + API_KEY);

                tripDtoList.add(new TripDto(tripEntity.getStartDate(), tripEntity.getEndDate(), startDateFormatted, endDateFormatted, startGeoPoint, startPlaceName, endGeoPoint, endPlaceName));
            }
            return new TripsDto(tripDtoList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new TripsDto();
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

    private Long getLongDate(String date) throws Exception {
        Date parsedDate = new SimpleDateFormat(dateFormatPattern).parse(date);
        return parsedDate.getTime();
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

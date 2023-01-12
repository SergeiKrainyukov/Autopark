package com.example.demo3.service;

import com.example.demo3.model.dto.GeoPointDto;
import com.example.demo3.model.dto.GeoPointsDto;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.EnterprisesRepository;
import com.example.demo3.repository.GeoPointRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringComponent
public class GeoPointsService {

    private final GeoPointRepository geoPointRepository;
    private final EnterprisesRepository enterprisesRepository;
    private final VehiclesRepository vehiclesRepository;

    private static final String dateFormatPattern = "dd.MM.yyyy HH:mm:ss";

    @Autowired
    public GeoPointsService(GeoPointRepository geoPointRepository, EnterprisesRepository enterprisesRepository, VehiclesRepository vehiclesRepository) {
        this.geoPointRepository = geoPointRepository;
        this.enterprisesRepository = enterprisesRepository;
        this.vehiclesRepository = vehiclesRepository;
    }

    public GeoPointsDto getGeoPointsDto(Long vehicleId, String dateFrom, String dateTo) {
        VehicleEntity vehicleEntity = vehiclesRepository.findById(vehicleId).orElse(null);
        if (vehicleEntity == null) return null;
        EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
        if (enterpriseEntity == null) return null;
        List<GeoPointEntity> geoPointEntities = getGeoPointEntities(vehicleId, dateFrom, dateTo);

        GeoPointsDto geoPointsDto = new GeoPointsDto();
        List<GeoPointDto> geoPointDtoList = new ArrayList<>();
        geoPointEntities.forEach(geoPointEntity -> geoPointDtoList.add(GeoPointDto.fromGeoPointEntity(geoPointEntity, getZonedTimeStringFormatted(enterpriseEntity.getTimeZone(), geoPointEntity.getDate()))));
        geoPointsDto.setGeoPoints(geoPointDtoList);
        return geoPointsDto;
    }

    private List<GeoPointEntity> getGeoPointEntities(Long vehicleId, String dateFrom, String dateTo) {
        List<GeoPointEntity> geoPointEntities = new ArrayList<>();
        geoPointRepository.findAll().forEach(geoPointEntities::add);

        long dateFromLong;
        long dateToLong;
        try {
            dateFromLong = getLongDate(dateFrom);
            dateToLong = getLongDate(dateTo);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
        geoPointEntities.removeIf(geoPointDto -> !geoPointDto.getVehicleId().equals(vehicleId));
        long finalDateFromLong = dateFromLong;
        geoPointEntities.removeIf(geoPointDto -> geoPointDto.getDate() < finalDateFromLong);
        long finalDateToLong = dateToLong;
        geoPointEntities.removeIf(geoPointDto -> geoPointDto.getDate() > finalDateToLong);
        return geoPointEntities;
    }

    public JSONObject getGeoJson(long vehicleId, String dateFrom, String dateTo) {

        List<GeoPointEntity> geoPointEntities = getGeoPointEntities(vehicleId, dateFrom, dateTo);

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
            System.out.println(featureCollection);
        }
        return featureCollection;
    }

    private Long getLongDate(String date) throws Exception {
        Date parsedDate = new SimpleDateFormat(dateFormatPattern).parse(date);
        return parsedDate.getTime();
    }

    private String getZonedTimeStringFormatted(String timezone, Long dateMillis) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date(dateMillis));
        DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.format(calendar.getTime());
    }
}

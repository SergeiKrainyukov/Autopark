package com.example.demo3.controller;

import com.example.demo3.common.Constants;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.EnterprisesRepository;
import com.example.demo3.repository.GeoPointRepository;
import com.example.demo3.repository.TripRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

@SpringComponent
public class DatabaseController {

    private final GeoPointRepository geoPointRepository;
    private final TripRepository tripRepository;
    private final EnterprisesRepository enterprisesRepository;
    private final VehiclesRepository vehiclesRepository;

    @Autowired
    public DatabaseController(GeoPointRepository geoPointRepository, TripRepository tripRepository, EnterprisesRepository enterprisesRepository, VehiclesRepository vehiclesRepository) {
        this.geoPointRepository = geoPointRepository;
        this.tripRepository = tripRepository;
        this.enterprisesRepository = enterprisesRepository;
        this.vehiclesRepository = vehiclesRepository;
    }

    public List<GeoPointEntity> getAllGeopoints() {
        Iterable<GeoPointEntity> geoPointEntities = geoPointRepository.findAll();
        List<GeoPointEntity> result = new ArrayList<>();
        for (GeoPointEntity geoPointEntity : geoPointEntities) {
            result.add(geoPointEntity);
        }
        return result;
    }

    public List<GeoPointEntity> getAllGeopointsByVehicleId(long vehicleId) {
        return geoPointRepository.findAllByVehicleId(vehicleId);
    }

    public List<GeoPointEntity> getAllGeopointsByVehicleIdAndDates(long vehicleId, String startDate, String endDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
        long dateFromInMillis;
        long dateToInMillis;
        try {
            dateFromInMillis = simpleDateFormat.parse(startDate).getTime();
            dateToInMillis = simpleDateFormat.parse(endDate).getTime();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
        return geoPointRepository.findAllByVehicleIdBetweenDates(dateFromInMillis, dateToInMillis, vehicleId);
    }

    public List<GeoPointEntity> getAllGeopointsByVehicleIdAndDates(long vehicleId, long startDate, long endDate) {
        return geoPointRepository.findAllByVehicleIdBetweenDates(startDate, endDate, vehicleId);
    }

    public List<TripEntity> getAllTrips() {
        Iterable<TripEntity> tripEntities = tripRepository.findAll();
        List<TripEntity> result = new ArrayList<>();
        for (TripEntity tripEntity : tripEntities) {
            result.add(tripEntity);
        }
        return result;
    }

    public List<TripEntity> getAllTripsByVehicleIdAndDates(long vehicleId, long startDate, long endDate) {
        return tripRepository.getAllByVehicleIdAndDates(vehicleId, startDate, endDate);
    }

    public List<TripEntity> getAllTripsByVehicleIdAndDates(long vehicleId, String startDate, String endDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
        long dateFromInMillis;
        long dateToInMillis;
        try {
            dateFromInMillis = simpleDateFormat.parse(startDate).getTime();
            dateToInMillis = simpleDateFormat.parse(endDate).getTime();
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return tripRepository.getAllByVehicleIdAndDates(vehicleId, dateFromInMillis, dateToInMillis);
    }

    public EnterpriseEntity getEnterpriseByVehicleId(long vehicleId) {
        VehicleEntity vehicleEntity = vehiclesRepository.findById(vehicleId).orElse(null);
        if (vehicleEntity == null) return null;
        return enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
    }


}

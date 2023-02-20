package com.example.demo3.controller;

import com.example.demo3.common.Constants;
import com.example.demo3.model.dto.VehicleDto;
import com.example.demo3.model.dto.VehiclesDto;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.GeoPointEntity;
import com.example.demo3.model.entity.TripEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.util.*;

@SpringComponent
public class DatabaseController {

    private final GeoPointRepository geoPointRepository;
    private final TripRepository tripRepository;
    private final EnterprisesRepository enterprisesRepository;
    private final VehiclesRepository vehiclesRepository;
    private final VehiclesRepositoryPaged vehiclesRepositoryPaged;

    @Autowired
    public DatabaseController(GeoPointRepository geoPointRepository, TripRepository tripRepository, EnterprisesRepository enterprisesRepository, VehiclesRepository vehiclesRepository, VehiclesRepositoryPaged vehiclesRepositoryPaged) {
        this.geoPointRepository = geoPointRepository;
        this.tripRepository = tripRepository;
        this.enterprisesRepository = enterprisesRepository;
        this.vehiclesRepository = vehiclesRepository;
        this.vehiclesRepositoryPaged = vehiclesRepositoryPaged;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN_EXTENDED);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN_EXTENDED);
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

    public VehiclesDto getAllVehiclesDto(){
        VehiclesDto vehiclesDto = new VehiclesDto();
        List<VehicleDto> vehicleDtoList = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehiclesRepository.findAll()) {
            EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
            if (enterpriseEntity == null) {
                vehicleDtoList.add(VehicleDto.fromVehicleEntity(vehicleEntity, null));
                continue;
            }
            String enterpriseTimeZone = enterpriseEntity.getTimeZone();
            vehicleDtoList.add(VehicleDto.fromVehicleEntity(vehicleEntity, enterpriseTimeZone));
        }
        vehiclesDto.setVehicles(vehicleDtoList);
        return vehiclesDto;
    }

    public VehiclesDto getAllWithDtoLimited(int page, int limit) {
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, limit);
        Page<VehicleEntity> vehicleEntityPage = vehiclesRepositoryPaged.findAll(pageable);
        List<VehicleEntity> vehicleEntityList = vehicleEntityPage.getContent();
        List<VehicleDto> vehicleDtoList = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehicleEntityList) {
            EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
            if (enterpriseEntity == null) {
                vehicleDtoList.add(VehicleDto.fromVehicleEntity(vehicleEntity, null));
                continue;
            }
            String enterpriseTimeZone = enterpriseEntity.getTimeZone();
            vehicleDtoList.add(VehicleDto.fromVehicleEntity(vehicleEntity, enterpriseTimeZone));
        }
        VehiclesDto vehiclesDto = new VehiclesDto();
        vehiclesDto.setVehicles(vehicleDtoList);
        return vehiclesDto;
    }

    public VehicleEntity saveVehicle(VehicleEntity vehicleEntity) {
        return vehiclesRepository.save(vehicleEntity);
    }

    public VehicleEntity updateVehicle(VehicleEntity vehicleEntity, Long id) {
        VehicleEntity vehicle = vehiclesRepository.findById(id).orElse(null);
        if (vehicle != null) return vehiclesRepository.save(vehicleEntity);
        else return null;
    }

    public VehicleEntity findVehicleById(Long id) {
        return vehiclesRepository.findById(id).orElse(null);
    }

    public void deleteVehicleById(Long id) {
        vehiclesRepository.deleteById(id);
    }


}

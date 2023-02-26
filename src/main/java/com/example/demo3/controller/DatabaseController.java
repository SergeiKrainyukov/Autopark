package com.example.demo3.controller;

import com.example.demo3.common.Constants;
import com.example.demo3.model.dto.*;
import com.example.demo3.model.entity.*;
import com.example.demo3.model.mock.MockObjectsCreator;
import com.example.demo3.repository.*;
import com.example.demo3.security.SecurityService;
import com.example.demo3.service.TripService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.example.demo3.common.DateFormatHelper.getZonedTimeStringFormatted;

@SpringComponent
public class DatabaseController {

    private final GeoPointRepository geoPointRepository;
    private final TripRepository tripRepository;
    private final EnterprisesRepository enterprisesRepository;
    private final VehiclesRepository vehiclesRepository;
    private final VehiclesRepositoryPaged vehiclesRepositoryPaged;
    private final DriversRepository driversRepository;
    private final ManagersRepository managersRepository;
    private final MockObjectsCreator mockObjectsCreator;
    private final BrandsRepository brandsRepository;
    private final SecurityService securityService;
    private final TripService tripService;

    @Autowired
    public DatabaseController(GeoPointRepository geoPointRepository, TripRepository tripRepository, EnterprisesRepository enterprisesRepository, VehiclesRepository vehiclesRepository, VehiclesRepositoryPaged vehiclesRepositoryPaged, DriversRepository driversRepository, ManagersRepository managersRepository, MockObjectsCreator mockObjectsCreator, BrandsRepository brandsRepository, SecurityService securityService, TripService tripService) {
        this.geoPointRepository = geoPointRepository;
        this.tripRepository = tripRepository;
        this.enterprisesRepository = enterprisesRepository;
        this.vehiclesRepository = vehiclesRepository;
        this.vehiclesRepositoryPaged = vehiclesRepositoryPaged;
        this.driversRepository = driversRepository;
        this.managersRepository = managersRepository;
        this.mockObjectsCreator = mockObjectsCreator;
        this.brandsRepository = brandsRepository;
        this.securityService = securityService;
        this.tripService = tripService;
    }

    public List<GeoPointEntity> getAllGeopoints() {
        Iterable<GeoPointEntity> geoPointEntities = geoPointRepository.findAll();
        List<GeoPointEntity> result = new ArrayList<>();
        for (GeoPointEntity geoPointEntity : geoPointEntities) {
            result.add(geoPointEntity);
        }
        return result;
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

    public List<TripDto> getAllTripsDtoByVehicleIdAndDates(Long vehicleId, long startDate, long endDate) {
        List<TripEntity> tripEntities = getAllTripsByVehicleIdAndDates(vehicleId, startDate, endDate);
        List<GeoPointEntity> geoPointEntities = getAllGeopointsByVehicleIdAndDates(vehicleId, startDate, endDate);
        if (tripEntities.size() != 0 && geoPointEntities.size() != 0) {
            return tripService.getAllTripsByVehicleIdAndDates(TimeZone.getDefault(), tripEntities, geoPointEntities).getTrips();
        } else {
            return new ArrayList<>();
        }
    }

    public EnterpriseEntity getEnterpriseByVehicleId(long vehicleId) {
        VehicleEntity vehicleEntity = vehiclesRepository.findById(vehicleId).orElse(null);
        if (vehicleEntity == null) return null;
        return enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
    }

    public VehiclesDto getAllVehiclesDto() {
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

    public void deleteVehicleById(Long id) {
        vehiclesRepository.deleteById(id);
    }

    public void deleteVehicle(VehicleEntity vehicleEntity) {
        vehiclesRepository.deleteById(vehicleEntity.getId());
    }

    public EnterprisesDto getAllEnterprisesDtoForManager(Long managerId) {
        ManagerEntity manager = managersRepository.findById(managerId).orElse(null);
        EnterprisesDto enterprisesDto = new EnterprisesDto();
        if (manager == null) return enterprisesDto;
        for (EnterpriseEntity enterpriseEntity : enterprisesRepository.findAllById(manager.getEnterprises())) {
            enterprisesDto.getEnterprises().add(enterpriseEntity);
        }
        return enterprisesDto;
    }

    public EnterpriseEntity createMockEnterprise(Long managerId) {
        EnterpriseEntity enterpriseEntity = mockObjectsCreator.createMockEnterprise();
        EnterpriseEntity createdEnterprise = enterprisesRepository.save(enterpriseEntity);
        ManagerEntity manager = managersRepository.findById(managerId).orElse(null);
        if (manager != null) {
            manager.getEnterprises().add(createdEnterprise.getId());
            managersRepository.save(manager);
        }
        return enterpriseEntity;
    }

    public DriversDto getAllDriversDto() {
        DriversDto driversDto = new DriversDto();
        for (DriverEntity driverEntity : driversRepository.findAll()) {
            driversDto.getDrivers().add(driverEntity);
        }
        return driversDto;
    }

    public GeoPointsDto getGeoPointsDto(long vehicleId, String startDate, String endDate) {
        GeoPointsDto geoPointsDto = new GeoPointsDto();
        List<GeoPointEntity> geoPointEntities = getAllGeopointsByVehicleIdAndDates(vehicleId, startDate, endDate);
        if (geoPointEntities == null || geoPointEntities.size() == 0) return geoPointsDto;
        VehicleEntity vehicleEntity = vehiclesRepository.findById(vehicleId).orElse(null);
        if (vehicleEntity == null) return geoPointsDto;
        EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
        if (enterpriseEntity == null) return geoPointsDto;
        List<GeoPointDto> geoPointDtoList = new ArrayList<>();
        geoPointEntities.forEach(geoPointEntity -> geoPointDtoList.add(GeoPointDto.fromGeoPointEntity(geoPointEntity, getZonedTimeStringFormatted(enterpriseEntity.getTimeZone(), geoPointEntity.getDate()))));
        geoPointsDto.setGeoPoints(geoPointDtoList);
        return geoPointsDto;
    }

    public List<BrandEntity> getAllBrands() {
        List<BrandEntity> brandEntities = new ArrayList<>();
        brandsRepository.findAll().forEach(brandEntities::add);
        return brandEntities;
    }

    public List<VehicleEntity> getAllVehicles() {
        List<VehicleEntity> vehicleEntities = new ArrayList<>();
        vehiclesRepository.findAll().forEach(vehicleEntities::add);
        return vehicleEntities;
    }

    public List<VehicleEntity> getAllVehiclesByEnterpriseId(long enterpriseId) {
        return new ArrayList<>(vehiclesRepository.findAllByEnterpriseId(enterpriseId));
    }

    public List<DriverEntity> getAllDrivers() {
        List<DriverEntity> driverEntities = new ArrayList<>();
        driversRepository.findAll().forEach(driverEntities::add);
        return driverEntities;
    }

    public List<EnterpriseEntity> getEnterprisesForCurrentManager() {
        List<EnterpriseEntity> enterprises = new ArrayList<>();
        for (EnterpriseEntity enterpriseEntity : enterprisesRepository.findAll()) {
            enterprises.add(enterpriseEntity);
        }
        ManagerEntity currentManager = getCurrentManager();
        if (currentManager != null)
            enterprises.removeIf(enterpriseEntity -> !currentManager.getEnterprises().contains(enterpriseEntity.getId()));
        return enterprises;
    }

    private ManagerEntity getCurrentManager() {
        String authenticatedManagerName = securityService.getAuthenticatedUser().getUsername();
        List<ManagerEntity> managers = getManagers();
        if (managers.isEmpty()) return null;
        managers.removeIf(manager -> !manager.getUsername().equals(authenticatedManagerName));
        return managers.get(0);
    }

    public List<ManagerEntity> getManagers() {
        List<ManagerEntity> managers = new ArrayList<>();
        for (ManagerEntity manager : managersRepository.findAll()) {
            managers.add(manager);
        }
        return managers;
    }

    public Long getVehicleIdByStateNumber(Integer stateNumber) {
        VehicleEntity vehicleEntity = vehiclesRepository.findVehicleByStateNumber(stateNumber);
        if (vehicleEntity != null) return vehicleEntity.getId();
        return -1L;
    }

    public VehicleEntity getVehicleByStateNumber(Integer stateNumber) {
        return vehiclesRepository.findVehicleByStateNumber(stateNumber);
    }

    public List<DriverEntity> getDriversByEnterpriseId(long enterpriseId) {
        List<DriverEntity> driverEntityList = driversRepository.getAllByEnterpriseId(enterpriseId);
        if (driverEntityList == null) return new ArrayList<>();
        return driverEntityList;
    }

}

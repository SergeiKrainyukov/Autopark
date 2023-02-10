package com.example.demo3.controller;

import com.example.demo3.common.utilities.GenerateRandomVehiclesUtility;
import com.example.demo3.common.utilities.TripOnMapGenerationUtility;
import com.example.demo3.model.dto.*;
import com.example.demo3.model.entity.*;
import com.example.demo3.model.mock.CreateRandVehiclesInfoDto;
import com.example.demo3.model.mock.MockObjectsCreator;
import com.example.demo3.repository.ManagersRepository;
import com.example.demo3.service.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(path = "/")
public class RestController {
    @Autowired
    private CRUDServiceFactory crudServiceFactory;
    @Autowired
    private GenerateRandomVehiclesUtility generateRandomVehiclesUtility;
    @Autowired
    private ManagersRepository managersRepository;
    @Autowired
    private GeoPointsService geoPointsService;
    @Autowired
    private TripService tripService;
    @Autowired
    private TripOnMapGenerationUtility tripOnMapGenerationUtility;
    @Autowired
    private ReportService reportService;
    @Autowired
    private MockObjectsCreator mockObjectsCreator;
    @Autowired
    private DatabaseController databaseController;

    @GetMapping(
            path = "/vehicles",
            produces = "application/json")
    public VehiclesDto getVehicles() {
        return ((VehiclesCRUDService) crudServiceFactory.getService(ServiceType.VEHICLES_SERVICE)).getAllWithDto();
    }

    @GetMapping(
            path = "/vehiclesLimited",
            produces = "application/json")
    public VehiclesDto getVehiclesPaging(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "limit", defaultValue = "3") int limit) {
        return ((VehiclesCRUDService) crudServiceFactory.getService(ServiceType.VEHICLES_SERVICE)).getAllWithDtoLimited(page, limit);
    }

    @GetMapping(
            path = "/enterprises",
            produces = "application/json")
    public EnterprisesDto getEnterprises(@RequestParam("managerId") String managerId) {
        return ((EnterprisesCRUDService) crudServiceFactory.getService(ServiceType.ENTERPRISES_SERVICE)).getAllWithDtoForManager(Long.parseLong(managerId));
    }

    @PostMapping(
            path = "/createEnterprise/{managerId}",
            produces = "application/json")
    public EnterpriseEntity createMockEnterprise(@PathVariable Long managerId) {
        EnterpriseEntity enterpriseEntity = mockObjectsCreator.createMockEnterprise();
        EnterprisesCRUDService enterprisesCRUDService = ((EnterprisesCRUDService) crudServiceFactory.getService(ServiceType.ENTERPRISES_SERVICE));
        EnterpriseEntity createdEnterprise = enterprisesCRUDService.save(enterpriseEntity);
        ManagerEntity manager = managersRepository.findById(managerId).orElse(null);
        if (manager != null) {
            manager.getEnterprises().add(createdEnterprise.getId());
            managersRepository.save(manager);
        }
        return createdEnterprise;
    }

    @GetMapping(
            path = "/drivers",
            produces = "application/json")
    public DriversDto getDrivers() {
        return ((DriversCRUDService) crudServiceFactory.getService(ServiceType.DRIVERS_SERVICE)).getAllWithDto();
    }

    @PostMapping("/createVehicle")
    public VehicleEntity createVehicle(@RequestBody VehicleEntity vehicleEntity) {
        return ((VehiclesCRUDService) crudServiceFactory.getService(ServiceType.VEHICLES_SERVICE)).save(vehicleEntity);
    }

    @PostMapping("/createRandomVehicles")
    public void createRandomVehicles(@RequestBody CreateRandVehiclesInfoDto createRandVehiclesInfoDto) {
        generateRandomVehiclesUtility.createRandomVehicles(createRandVehiclesInfoDto);
    }

    @PutMapping("/updateVehicle/{id}")
    public VehicleEntity updateVehicle(@RequestBody VehicleEntity updatedVehicleEntity, @PathVariable Long id) {
        return ((VehiclesCRUDService) crudServiceFactory.getService(ServiceType.VEHICLES_SERVICE)).update(updatedVehicleEntity, id);
    }

    @DeleteMapping("/deleteVehicle/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        crudServiceFactory.getService(ServiceType.VEHICLES_SERVICE).deleteById(id);
    }

    @GetMapping("/geopoints")
    public GeoPointsDto getGeopointsForVehicle(@RequestParam(value = "vehicleId", defaultValue = "0") int vehicleId,
                                               @RequestParam(value = "dateFrom", defaultValue = "") String dateFrom,
                                               @RequestParam(value = "dateTo", defaultValue = "") String dateTo) {
        return geoPointsService.getGeoPointsDto((long) vehicleId, dateFrom, dateTo);
    }

    @GetMapping("/geopoints/geoJson")
    public Map<String, Object> getGeopointsForVehicleAsGeoJSON(@RequestParam(value = "vehicleId", defaultValue = "0") int vehicleId,
                                                               @RequestParam(value = "dateFrom", defaultValue = "") String dateFrom,
                                                               @RequestParam(value = "dateTo", defaultValue = "") String dateTo) {
        return geoPointsService.getGeoJson(vehicleId, dateFrom, dateTo).toMap();
    }

    @GetMapping("/trip")
    public Map<String, Object> getTrip(
            @RequestParam(value = "vehicleId", defaultValue = "0") int vehicleId,
            @RequestParam(value = "dateFrom", defaultValue = "") String dateFrom,
            @RequestParam(value = "dateTo", defaultValue = "") String dateTo
    ) {
        List<TripEntity> tripEntityList = databaseController.getAllTripsByVehicleIdAndDates(vehicleId, dateFrom, dateTo);
        List<GeoPointEntity> geoPointEntities = databaseController.getAllGeopoints();
        JSONObject jsonObject = tripService.getTripAsJSON(tripEntityList, geoPointEntities);
        return jsonObject.toMap();
    }

    @GetMapping("/trips")
    public TripsDto getTrips(
            @RequestParam(value = "vehicleId", defaultValue = "0") long vehicleId,
            @RequestParam(value = "dateFrom", defaultValue = "") String dateFrom,
            @RequestParam(value = "dateTo", defaultValue = "") String dateTo
    ) {
        EnterpriseEntity enterpriseEntity = databaseController.getEnterpriseByVehicleId(vehicleId);
        if (enterpriseEntity == null) return new TripsDto();
        List<TripEntity> tripEntities = databaseController.getAllTripsByVehicleIdAndDates(vehicleId, dateFrom, dateTo);
        if (tripEntities.size() == 0) return new TripsDto();
        List<GeoPointEntity> geoPointEntities = databaseController.getAllGeopointsByVehicleIdAndDates(vehicleId, dateFrom, dateTo);
        if (geoPointEntities.size() == 0) return new TripsDto();
        return tripService.getAllTripsByVehicleIdAndDates(TimeZone.getTimeZone(enterpriseEntity.getTimeZone()), tripEntities, geoPointEntities);
    }

    @PostMapping("/generateTrip")
    public void generateTrip(@RequestBody TripGenerationParametersDto tripGenerationParametersDto) {
        tripOnMapGenerationUtility.startGeneratingRoute(tripGenerationParametersDto);
    }

    @GetMapping("/report")
    public ReportDto getReport(
            @RequestBody ReportInfoDto reportInfoDto
    ) {
        List<TripEntity> tripsByVehicleIdAndDates = databaseController.getAllTripsByVehicleIdAndDates(reportInfoDto.getVehicleId(), reportInfoDto.getStringDateFrom(), reportInfoDto.getStringDateTo());
        return reportService.getReport(reportInfoDto, tripsByVehicleIdAndDates);
    }
}



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
        return databaseController.getAllVehiclesDto();
    }

    @GetMapping(
            path = "/vehiclesLimited",
            produces = "application/json")
    public VehiclesDto getVehiclesPaging(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "limit", defaultValue = "3") int limit) {
        return databaseController.getAllWithDtoLimited(page, limit);
    }

    @GetMapping(
            path = "/enterprises",
            produces = "application/json")
    public EnterprisesDto getEnterprises(@RequestParam("managerId") String managerId) {
        return databaseController.getAllEnterprisesDtoForManager(Long.parseLong(managerId));
    }

    @PostMapping(
            path = "/createEnterprise/{managerId}",
            produces = "application/json")
    public EnterpriseEntity createMockEnterprise(@PathVariable Long managerId) {
        return databaseController.createMockEnterprise(managerId);
    }

    @GetMapping(
            path = "/drivers",
            produces = "application/json")
    public DriversDto getDrivers() {
        return databaseController.getAllDriversDto();
    }

    @PostMapping("/createVehicle")
    public VehicleEntity createVehicle(@RequestBody VehicleEntity vehicleEntity) {
        return databaseController.saveVehicle(vehicleEntity);
    }

    @PostMapping("/createRandomVehicles")
    public void createRandomVehicles(@RequestBody CreateRandVehiclesInfoDto createRandVehiclesInfoDto) {
        generateRandomVehiclesUtility.createRandomVehicles(createRandVehiclesInfoDto);
    }

    @PutMapping("/updateVehicle/{id}")
    public VehicleEntity updateVehicle(@RequestBody VehicleEntity updatedVehicleEntity, @PathVariable Long id) {
        return databaseController.updateVehicle(updatedVehicleEntity, id);
    }

    @DeleteMapping("/deleteVehicle/{id}")
    public void deleteVehicle(@PathVariable Long id) {
        databaseController.deleteVehicleById(id);
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
        return tripService.getTripAsJSON(tripEntityList, geoPointEntities).toMap();
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



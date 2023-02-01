package com.example.demo3.common.utilities;

import com.example.demo3.model.BrandType;
import com.example.demo3.model.entity.*;
import com.example.demo3.model.mock.CreateRandVehiclesInfoDto;
import com.example.demo3.model.mock.MockObjectsCreator;
import com.example.demo3.repository.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringComponent
public class GenerateRandomVehiclesUtility {
    @Autowired
    private BrandsRepository brandsRepository;
    @Autowired
    private VehiclesRepository vehiclesRepository;
    @Autowired
    private MockObjectsCreator mockObjectsCreator;
    @Autowired
    private EnterprisesRepository enterprisesRepository;
    @Autowired
    private DriversRepository driversRepository;
    @Autowired
    private ManagersRepository managersRepository;

    private final Random random = new Random();

    public void createRandomVehicles(CreateRandVehiclesInfoDto createRandVehiclesInfoDto) {
        List<VehicleEntity> vehicleEntities = fillVehicleEntities(createRandVehiclesInfoDto);
        fillDriversEntities(createRandVehiclesInfoDto, vehicleEntities);
        ManagerEntity manager = managersRepository.findById(createRandVehiclesInfoDto.getManagerId()).orElse(null);
        if (manager == null) return;
        for (Long enterpriseId : createRandVehiclesInfoDto.getEnterprises()) {
            manager.getEnterprises().add(enterpriseId);
        }
        managersRepository.save(manager);
    }

    private List<VehicleEntity> fillVehicleEntities(CreateRandVehiclesInfoDto createRandVehiclesInfoDto) {
        List<BrandEntity> brandEntities = getBrandEntities();
        if (brandEntities.size() == 0) {
            brandsRepository.save(new BrandEntity("Brand Cargo", BrandType.CARGO, 60, 25, 2));
            brandsRepository.save(new BrandEntity("Brand Bus", BrandType.BUS, 70, 15, 25));
            brandsRepository.save(new BrandEntity("Brand Passenger", BrandType.PASSENGER, 50, 5, 5));
            brandEntities = getBrandEntities();
        }
        List<Long> enterprisesId = createRandVehiclesInfoDto.getEnterprises();
        List<VehicleEntity> vehicleEntities = new ArrayList<>();
        for (int i = 0; i < createRandVehiclesInfoDto.getVehiclesCount(); i++) {
            Long enterpriseId = enterprisesId.get(random.nextInt(enterprisesId.size()));
            VehicleEntity vehicleEntity = vehiclesRepository.save(mockObjectsCreator.createMockVehicleForEnterpriseAndBrand(enterpriseId, brandEntities.get(random.nextInt(brandEntities.size())).getId()));
            EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(enterpriseId).orElse(null);
            if (enterpriseEntity != null) {
                enterpriseEntity.getVehicles().add(vehicleEntity.getId());
                enterprisesRepository.save(enterpriseEntity);
                vehicleEntities.add(vehicleEntity);
            }
        }
        return vehicleEntities;
    }

    private void fillDriversEntities(CreateRandVehiclesInfoDto createRandVehiclesInfoDto, List<VehicleEntity> vehicleEntities) {
        for (int i = 0; i < createRandVehiclesInfoDto.getDriversCount(); i++) {
            VehicleEntity vehicleEntity = vehicleEntities.get(random.nextInt(vehicleEntities.size()));
            EnterpriseEntity enterpriseEntity = enterprisesRepository.findById(vehicleEntity.getEnterpriseId()).orElse(null);
            DriverEntity driverEntity = getDriverEntity(vehicleEntity);
            enterpriseEntity.getDrivers().add(driverEntity.getId());
            vehicleEntity.getDrivers().add(driverEntity.getId());
            enterprisesRepository.save(enterpriseEntity);
            vehiclesRepository.save(vehicleEntity);
        }
    }

    private List<BrandEntity> getBrandEntities() {
        List<BrandEntity> brandEntities = new ArrayList<>();
        for (BrandEntity brandEntity : brandsRepository.findAll()) {
            brandEntities.add(brandEntity);
        }
        return brandEntities;
    }

    private DriverEntity getDriverEntity(VehicleEntity vehicleEntity) {
        boolean driverForVehicle = random.nextBoolean();
        DriverEntity driverEntity;
        if (driverForVehicle) {
            driverEntity = driversRepository.save(mockObjectsCreator.createMockDriverForEnterpriseAndVehicle(vehicleEntity.getEnterpriseId(), vehicleEntity.getId()));
        } else {
            driverEntity = driversRepository.save(mockObjectsCreator.createMockDriverForEnterprise(vehicleEntity.getEnterpriseId()));
        }
        return driverEntity;
    }
}

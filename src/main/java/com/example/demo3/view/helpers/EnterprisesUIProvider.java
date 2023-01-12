package com.example.demo3.view.helpers;

import com.example.demo3.model.entity.DriverEntity;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.ManagerEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.DriversRepository;
import com.example.demo3.repository.EnterprisesRepository;
import com.example.demo3.repository.ManagersRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.example.demo3.security.SecurityService;
import com.example.demo3.view.EnterpriseUi;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringComponent
public class EnterprisesUIProvider {

    private final EnterprisesRepository enterprisesRepository;
    private final DriversRepository driversRepository;
    private final ManagersRepository managersRepository;
    private final VehiclesRepository vehiclesRepository;
    private final SecurityService securityService;

    @Autowired
    public EnterprisesUIProvider(EnterprisesRepository enterprisesRepository, DriversRepository driversRepository, ManagersRepository managersRepository, VehiclesRepository vehiclesRepository, SecurityService securityService) {
        this.enterprisesRepository = enterprisesRepository;
        this.driversRepository = driversRepository;
        this.managersRepository = managersRepository;
        this.vehiclesRepository = vehiclesRepository;
        this.securityService = securityService;
    }

    public List<EnterpriseUi> getEnterprisesUIForCurrentManager() {
        return getEnterprisesUi(getEnterprisesForCurrentManager());
    }

    private List<EnterpriseUi> getEnterprisesUi(List<EnterpriseEntity> enterpriseEntities) {
        List<EnterpriseUi> enterpriseUis = new ArrayList<>();
        for (EnterpriseEntity enterpriseEntity : enterpriseEntities) {

            String name = enterpriseEntity.getName();
            String city = enterpriseEntity.getCity();

            List<VehicleEntity> vehicleEntities = new ArrayList<>();
            for (VehicleEntity vehicleEntity : vehiclesRepository.findAllById(enterpriseEntity.getVehicles())) {
                vehicleEntities.add(vehicleEntity);
            }

            List<DriverEntity> driverEntities = new ArrayList<>();
            for (DriverEntity driverEntity : driversRepository.findAllById(enterpriseEntity.getDrivers())) {
                driverEntities.add(driverEntity);
            }

            String vehicles = vehicleEntities.stream().map(VehicleEntity::getStateNumber).collect(Collectors.joining(", "));
            String drivers = driverEntities.stream().map(DriverEntity::getName).collect(Collectors.joining(", "));

            enterpriseUis.add(new EnterpriseUi(enterpriseEntity.getId(), name, city, vehicles, drivers));
        }
        return enterpriseUis;
    }

    private List<ManagerEntity> getManagers() {
        List<ManagerEntity> managers = new ArrayList<>();
        for (ManagerEntity manager : managersRepository.findAll()) {
            managers.add(manager);
        }
        return managers;
    }

    private ManagerEntity getCurrentManager() {
        String authenticatedManagerName = securityService.getAuthenticatedUser().getUsername();
        List<ManagerEntity> managers = getManagers();
        if (managers.isEmpty()) return null;
        managers.removeIf(manager -> !manager.getUsername().equals(authenticatedManagerName));
        return managers.get(0);
    }

    private List<EnterpriseEntity> getEnterprisesForCurrentManager() {
        List<EnterpriseEntity> enterprises = new ArrayList<>();
        for (EnterpriseEntity enterpriseEntity : enterprisesRepository.findAll()) {
            enterprises.add(enterpriseEntity);
        }
        ManagerEntity currentManager = getCurrentManager();
        if (currentManager != null)
            enterprises.removeIf(enterpriseEntity -> !currentManager.getEnterprises().contains(enterpriseEntity.getId()));
        return enterprises;
    }
}

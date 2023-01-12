package com.example.demo3.service;

import com.example.demo3.model.dto.DriversDto;
import com.example.demo3.model.entity.DriverEntity;
import com.example.demo3.repository.DriversRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class DriversCRUDService implements CRUDService<DriversDto, DriverEntity> {
    @Autowired
    private DriversRepository driversRepository;

    @Override
    public DriversDto getAllWithDto() {
        DriversDto driversDto = new DriversDto();
        for (DriverEntity driverEntity : driversRepository.findAll()) {
            driversDto.getDrivers().add(driverEntity);
        }
        return driversDto;
    }

    @Override
    public DriverEntity save(DriverEntity driverEntity) {
        return driversRepository.save(driverEntity);
    }

    @Override
    public DriverEntity update(DriverEntity driverEntity, Long id) {
        DriverEntity driver = driversRepository.findById(id).orElse(null);
        if (driver != null) return driversRepository.save(driverEntity);
        else return null;
    }

    @Override
    public DriverEntity findById(Long id) {
        return driversRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        driversRepository.deleteById(id);
    }
}

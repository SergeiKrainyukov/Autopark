package com.example.demo3.service;

import com.example.demo3.model.dto.VehicleDto;
import com.example.demo3.model.dto.VehiclesDto;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.EnterprisesRepository;
import com.example.demo3.repository.VehiclesRepository;
import com.example.demo3.repository.VehiclesRepositoryPaged;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@SpringComponent
public class VehiclesCRUDService implements CRUDService<VehiclesDto, VehicleEntity> {
    @Autowired
    private VehiclesRepository vehiclesRepository;
    @Autowired
    private VehiclesRepositoryPaged vehiclesRepositoryPaged;
    @Autowired
    private EnterprisesRepository enterprisesRepository;

    @Override
    public VehiclesDto getAllWithDto() {
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
        Page<VehicleEntity> usersPage = vehiclesRepositoryPaged.findAll(pageable);

        List<VehicleEntity> vehicleEntityList = usersPage.getContent();
        VehiclesDto vehiclesDto = new VehiclesDto();
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
        vehiclesDto.setVehicles(vehicleDtoList);
        return vehiclesDto;
    }

    @Override
    public VehicleEntity save(VehicleEntity vehicleEntity) {
        return vehiclesRepository.save(vehicleEntity);
    }

    @Override
    public VehicleEntity update(VehicleEntity vehicleEntity, Long id) {
        VehicleEntity vehicle = vehiclesRepository.findById(id).orElse(null);
        if (vehicle != null) return vehiclesRepository.save(vehicleEntity);
        else return null;
    }

    @Override
    public VehicleEntity findById(Long id) {
        return vehiclesRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        vehiclesRepository.deleteById(id);
    }

}

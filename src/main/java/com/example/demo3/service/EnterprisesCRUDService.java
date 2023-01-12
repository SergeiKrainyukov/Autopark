package com.example.demo3.service;

import com.example.demo3.model.dto.EnterprisesDto;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.ManagerEntity;
import com.example.demo3.repository.EnterprisesRepository;
import com.example.demo3.repository.ManagersRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class EnterprisesCRUDService implements CRUDService<EnterprisesDto, EnterpriseEntity> {
    @Autowired
    private EnterprisesRepository enterprisesRepository;
    @Autowired
    private ManagersRepository managersRepository;

    @Override
    public EnterprisesDto getAllWithDto() {
        EnterprisesDto enterprisesDto = new EnterprisesDto();
        for (EnterpriseEntity enterpriseEntity : enterprisesRepository.findAll()) {
            enterprisesDto.getEnterprises().add(enterpriseEntity);
        }
        return enterprisesDto;
    }

    public EnterprisesDto getAllWithDtoForManager(Long managerId) {
        ManagerEntity manager = managersRepository.findById(managerId).orElse(null);
        EnterprisesDto enterprisesDto = new EnterprisesDto();
        if (manager == null) return enterprisesDto;
        for (EnterpriseEntity enterpriseEntity : enterprisesRepository.findAllById(manager.getEnterprises())) {
            enterprisesDto.getEnterprises().add(enterpriseEntity);
        }
        return enterprisesDto;
    }

    @Override
    public EnterpriseEntity save(EnterpriseEntity enterpriseEntity) {
        return enterprisesRepository.save(enterpriseEntity);
    }

    @Override
    public EnterpriseEntity update(EnterpriseEntity enterpriseEntity, Long id) {
        EnterpriseEntity enterprise = enterprisesRepository.findById(id).orElse(null);
        if (enterprise != null) return enterprisesRepository.save(enterpriseEntity);
        else return null;
    }

    @Override
    public EnterpriseEntity findById(Long id) {
        return enterprisesRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        enterprisesRepository.deleteById(id);
    }
}

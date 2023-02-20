package com.example.demo3.service;

import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
public class CRUDServiceFactory {
    @Autowired
    EnterprisesCRUDService enterprisesCRUDService;
    @Autowired
    DriversCRUDService driversCRUDService;

    public CRUDService<?, ?> getService(ServiceType serviceType) {
        switch (serviceType) {
            case ENTERPRISES_SERVICE: return enterprisesCRUDService;
            case DRIVERS_SERVICE: return driversCRUDService;
            default: return null;
        }
    }
}

package com.example.demo3.view;

public class EnterpriseUi {
    private Long id;
    private String name;
    private String city;
    private String vehicles;
    private String drivers;

    public EnterpriseUi(Long id, String name, String city, String vehicles, String drivers) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.vehicles = vehicles;
        this.drivers = drivers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVehicles() {
        return vehicles;
    }

    public void setVehicles(String vehicles) {
        this.vehicles = vehicles;
    }

    public String getDrivers() {
        return drivers;
    }

    public void setDrivers(String drivers) {
        this.drivers = drivers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

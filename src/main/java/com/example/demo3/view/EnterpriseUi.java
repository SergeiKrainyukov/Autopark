package com.example.demo3.view;

public class EnterpriseUi {
    private Long id;
    private String name;
    private String city;
    private String vehicleNumbers;
    private String driverNames;

    public EnterpriseUi(Long id, String name, String city, String vehicleNumbers, String driverNames) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.vehicleNumbers = vehicleNumbers;
        this.driverNames = driverNames;
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

    public String getVehicleNumbers() {
        return vehicleNumbers;
    }

    public void setVehicleNumbers(String vehicleNumbers) {
        this.vehicleNumbers = vehicleNumbers;
    }

    public String getDriverNames() {
        return driverNames;
    }

    public void setDriverNames(String driverNames) {
        this.driverNames = driverNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

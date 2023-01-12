package com.example.demo3.model.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class EnterpriseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String city;
    private String timeZone;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> vehicles;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> drivers;

    public EnterpriseEntity() {
    }

    public EnterpriseEntity(String name, String city) {
        this.name = name;
        this.city = city;
        vehicles = new HashSet<>();
        drivers = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<Long> getVehicles() {
        return vehicles;
    }

    public void setVehicles(Set<Long> vehicles) {
        this.vehicles = vehicles;
    }

    public Set<Long> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Long> drivers) {
        this.drivers = drivers;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}

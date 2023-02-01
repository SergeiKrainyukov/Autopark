package com.example.demo3.model.mock;

import com.example.demo3.model.BrandType;
import com.example.demo3.model.entity.BrandEntity;
import com.example.demo3.model.entity.DriverEntity;
import com.example.demo3.model.entity.EnterpriseEntity;
import com.example.demo3.model.entity.VehicleEntity;
import com.example.demo3.repository.VehiclesRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SpringComponent
public class MockObjectsCreator {

    private final Random random = new Random();
    private final VehiclesRepository vehiclesRepository;

    @Autowired
    public MockObjectsCreator(VehiclesRepository vehiclesRepository) {
        this.vehiclesRepository = vehiclesRepository;
    }

    public EnterpriseEntity createMockEnterprise() {
        EnterpriseEntity enterpriseEntity = new EnterpriseEntity("Enterp. " + createRandomString(5), "City " + createRandomString(5));
        enterpriseEntity.setTimeZone(createRandomTimeZone());
        return enterpriseEntity;
    }

    public DriverEntity createMockDriverForEnterprise(Long enterpriseId) {
        return new DriverEntity(createRandomName(), createRandomSalary(), enterpriseId);
    }

    public DriverEntity createMockDriverForEnterpriseAndVehicle(Long enterpriseId, Long vehicleId) {
        return new DriverEntity(createRandomName(), createRandomSalary(), enterpriseId, vehicleId, random.nextBoolean());
    }

    public VehicleEntity createMockVehicleForEnterpriseAndBrand(Long enterpriseId, Long brandId) {
        return new VehicleEntity(createRandomStateNumber(), createRandomPrice(), createRandomYear(), createRandomMileage(), enterpriseId, brandId, createRandomPurchaseDate());
    }

    private Integer createRandomStateNumber() {
        Set<Integer> stateNumbersSet = new HashSet<>();
        Iterable<VehicleEntity> vehicleEntities = vehiclesRepository.findAll();
        for (VehicleEntity vehicleEntity : vehicleEntities) {
            stateNumbersSet.add(vehicleEntity.getStateNumber());
        }
        int number = random.nextInt(999_999);
        while (stateNumbersSet.contains(number)) {
            number = random.nextInt(999_999);
        }
        return number;
    }

    private String createRandomString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (random.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }

    private String createRandomName() {
        String[] names = new String[]{
                "John",
                "Mike",
                "Jane",
                "Steve",
                "Tony",
                "Edgar",
                "Don",
                "Raf",
                "Mary",
                "Carlos",
                "Luke",
                "Ivan",
                "Denis",
                "Dan",
                "Daria",
                "Aira",
                "Clair",
                "Lucius",
                "Bruce",
                "Bob",
                "Sarah",
                "Robert",
                "Jason",
                "Matt",
        };
        return names[random.nextInt(names.length)];
    }

    private int createRandomSalary() {
        return random.nextInt(500_000 - 10_000) + 10_000;
    }

    private int createRandomPrice() {
        return random.nextInt(20_000_000 - 200_000) + 200_000;
    }

    private int createRandomYear() {
        return random.nextInt(2022 - 1950) + 1950;
    }

    private int createRandomMileage() {
        return random.nextInt(100_000);
    }

    private BrandType createRandomBrandType() {
        return BrandType.values()[random.nextInt(BrandType.values().length)];
    }

    private int createRandomTank() {
        return random.nextInt(100 - 40) + 40;
    }

    private int createRandomLoadCapacity() {
        return random.nextInt(20 - 1) + 1;
    }

    private int createRandomNumberOfSeats() {
        return random.nextInt(20 - 2) + 2;
    }

    private Long createRandomPurchaseDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        calendar.add(Calendar.DAY_OF_MONTH, (random.nextInt(26 - 1) + 1) * (-1));
        calendar.add(Calendar.HOUR, (random.nextInt(10 - 1) + 1) * (-1));
        calendar.add(Calendar.MINUTE, (random.nextInt(50 - 1) + 1));

        return calendar.getTime().getTime();
    }

    private String createRandomTimeZone() {
        String[] zones = new String[]{
                "Europe/Moscow",
                "Europe/London"
        };
        return zones[random.nextInt(zones.length)];
    }


}

package ru.itmo.vehiclelab;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.itmo.vehiclelab.domain.FuelType;
import ru.itmo.vehiclelab.domain.Vehicle;
import ru.itmo.vehiclelab.domain.VehicleType;
import ru.itmo.vehiclelab.repository.VehicleRepository;
import ru.itmo.vehiclelab.service.SpecialOperationsService;
import ru.itmo.vehiclelab.service.VehicleService;
import ru.itmo.vehiclelab.web.VehicleForm;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VehicleWorkflowTests {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private SpecialOperationsService operationsService;

    @Autowired
    private VehicleRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsAndUpdatesVehicleWithGeneratedFields() {
        Vehicle created = vehicleService.create(form("Аврора", VehicleType.SHIP, FuelType.DIESEL, 320, 18.5));

        assertThat(created.getId()).isPositive();
        assertThat(created.getCreationDate()).isNotNull();

        VehicleForm update = form("Аврора-2", VehicleType.SHIP, FuelType.ALCOHOL, 360, 17.0);
        vehicleService.update(created.getId(), update);

        Vehicle stored = vehicleService.get(created.getId());
        assertThat(stored.getName()).isEqualTo("Аврора-2");
        assertThat(stored.getFuelType()).isEqualTo(FuelType.ALCOHOL);
        assertThat(stored.getEnginePower()).isEqualTo(360);
    }

    @Test
    void performsAllQueryOperationsInBusinessLayer() {
        vehicleService.create(form("Самолёт A", VehicleType.PLANE, FuelType.DIESEL, 500, 30.0));
        vehicleService.create(form("Катер B", VehicleType.BOAT, FuelType.ALCOHOL, 150, 12.0));
        vehicleService.create(form("Самолёт C", VehicleType.PLANE, FuelType.DIESEL, 700, 30.0));

        assertThat(operationsService.countByFuelConsumptionGreaterThan(20.0)).isEqualTo(2);
        assertThat(operationsService.findByType(VehicleType.PLANE)).hasSize(2);

        List<Vehicle> powerRange = operationsService.findByEnginePowerRange(200, 600);
        assertThat(powerRange).extracting(Vehicle::getName).containsExactly("Самолёт A");

        assertThat(operationsService.deleteOneByFuelConsumption(30.0)).isPresent();
        assertThat(repository.count()).isEqualTo(2);

        assertThat(operationsService.deleteAllByFuelType(FuelType.DIESEL)).isEqualTo(1);
        assertThat(repository.count()).isEqualTo(1);
    }

    private VehicleForm form(String name, VehicleType type, FuelType fuelType,
                             int enginePower, double consumption) {
        VehicleForm form = new VehicleForm();
        form.setName(name);
        form.setCoordinateX(10.5);
        form.setCoordinateY(120.0f);
        form.setType(type);
        form.setEnginePower(enginePower);
        form.setNumberOfWheels(4);
        form.setCapacity(12.0);
        form.setDistanceTravelled(1000);
        form.setFuelConsumption(consumption);
        form.setFuelType(fuelType);
        return form;
    }
}

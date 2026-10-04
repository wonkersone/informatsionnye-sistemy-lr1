package ru.itmo.vehiclelab;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.itmo.vehiclelab.repository.*;
import ru.itmo.vehiclelab.service.StartupData;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class StartupDataTests {
    @Autowired StartupData data;
    @Autowired SeedStateRepository seeds;
    @Autowired VehicleRepository vehicles;
    @Autowired CoordinatesRepository coordinates;

    @Test
    void createsExamplesOnlyOnceAndDoesNotRestoreDeletedVehicle() {
        vehicles.deleteAll();
        coordinates.deleteAll();
        seeds.deleteAll();
        data.addVehicles();
        assertThat(vehicles.count()).isEqualTo(24);
        assertThat(coordinates.count()).isEqualTo(6);
        vehicles.deleteById(vehicles.findAll().getFirst().getId());
        data.addVehicles();
        assertThat(vehicles.count()).isEqualTo(23);
        vehicles.deleteAll();
        coordinates.deleteAll();
        seeds.deleteAll();
    }
}

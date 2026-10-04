package ru.itmo.vehiclelab.service;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.vehiclelab.domain.*;
import ru.itmo.vehiclelab.repository.SeedStateRepository;
import ru.itmo.vehiclelab.web.VehicleForm;

@Component
public class StartupData implements ApplicationRunner {
    private final UserService users;
    private final CoordinatesService coordinates;
    private final VehicleService vehicles;
    private final SeedStateRepository seeds;
    private final Environment environment;

    public StartupData(UserService users, CoordinatesService coordinates, VehicleService vehicles,
                       SeedStateRepository seeds, Environment environment) {
        this.users = users;
        this.coordinates = coordinates;
        this.vehicles = vehicles;
        this.seeds = seeds;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        users.ensureUser(environment.getProperty("LAB_USER", "student"), environment.getProperty("LAB_PASSWORD", "student123"));
        users.ensureUser(environment.getProperty("LAB_SECOND_USER", "teacher"), environment.getProperty("LAB_SECOND_PASSWORD", "teacher123"));
        if (environment.getProperty("lab.seed-data", Boolean.class, true)) addVehicles();
    }

    @Transactional
    public void addVehicles() {
        if (seeds.existsById("sample-vehicles")) return;
        Integer[] points = new Integer[6];
        for (int i = 0; i < points.length; i++) {
            points[i] = coordinates.create(30.0 + i * 2, 55.0f + i).getId();
        }
        String[][] names = {
                {"Ан-2", "Як-18", "Ил-76", "Ту-204", "Бе-200", "Ан-24", "Як-40", "Ил-114"},
                {"Ладога", "Нева", "Волна", "Чайка", "Риф", "Стрела", "Бриз", "Ветер"},
                {"Аврора", "Балтика", "Волга", "Север", "Океан", "Полярный", "Витязь", "Маяк"}
        };
        VehicleType[] types = VehicleType.values();
        FuelType[] fuels = FuelType.values();
        for (int group = 0; group < names.length; group++) {
            for (int i = 0; i < names[group].length; i++) {
                VehicleForm form = new VehicleForm();
                form.setName(names[group][i]);
                form.setCoordinatesId(points[(group * 2 + i % 2) % points.length]);
                form.setType(types[group]);
                form.setEnginePower(100 + group * 500 + i * 150);
                form.setNumberOfWheels(group == 0 ? 3 + i % 3 : 1);
                form.setCapacity(group == 0 ? 8.0 + i * 20 : 2.0 + group * 15 + i * 5);
                form.setDistanceTravelled(1200 + group * 10000 + i * 2500);
                form.setFuelConsumption(5.0 + group * 20 + i % 4 * 5);
                form.setFuelType(fuels[(group + i) % fuels.length]);
                vehicles.create(form);
            }
        }
        seeds.save(new SeedState("sample-vehicles"));
        org.slf4j.LoggerFactory.getLogger("lab").info("Добавлены тестовые данные: 24 транспорта и 6 наборов координат");
    }
}

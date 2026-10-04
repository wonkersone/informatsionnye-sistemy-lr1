package ru.itmo.vehiclelab.service;

import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.itmo.vehiclelab.repository.*;
import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class StartupLog {
    private final VehicleRepository vehicles;
    private final CoordinatesRepository coordinates;
    private final UserAccountRepository users;
    private final DataSource dataSource;

    public StartupLog(VehicleRepository vehicles, CoordinatesRepository coordinates,
                      UserAccountRepository users, DataSource dataSource) {
        this.vehicles = vehicles;
        this.coordinates = coordinates;
        this.users = users;
        this.dataSource = dataSource;
    }

    @EventListener
    public void ready(ApplicationReadyEvent event) throws SQLException {
        if (!(event.getApplicationContext() instanceof ServletWebServerApplicationContext context)) return;
        var log = LoggerFactory.getLogger("lab");
        try (var connection = dataSource.getConnection()) {
            String database = connection.getMetaData().getDatabaseProductName();
            log.info("База данных: {}. Подключение установлено", database);
            if ("H2".equals(database)) log.warn("Демо-режим: данные будут удалены после остановки");
        }
        log.info("В базе: транспорт - {}, координаты - {}, пользователи - {}", vehicles.count(), coordinates.count(), users.count());
        log.info("Подробности и ошибки: logs/application.log");
        log.info("Приложение готово: http://localhost:{}", context.getWebServer().getPort());
    }
}

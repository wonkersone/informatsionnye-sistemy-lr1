package ru.itmo.vehiclelab.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.vehiclelab.domain.FuelType;
import ru.itmo.vehiclelab.domain.Vehicle;
import ru.itmo.vehiclelab.domain.VehicleType;
import ru.itmo.vehiclelab.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialOperationsService {

    private final VehicleRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public SpecialOperationsService(VehicleRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public long deleteAllByFuelType(FuelType fuelType) {
        long deleted = repository.deleteByFuelType(fuelType);
        if (deleted > 0) {
            eventPublisher.publishEvent(new VehicleChangedEvent("BULK_DELETED", null));
        }
        return deleted;
    }

    @Transactional
    public Optional<Vehicle> deleteOneByFuelConsumption(Double value) {
        Optional<Vehicle> candidate = repository.findFirstByFuelConsumptionOrderByIdAsc(value);
        candidate.ifPresent(vehicle -> {
            Integer id = vehicle.getId();
            repository.delete(vehicle);
            eventPublisher.publishEvent(new VehicleChangedEvent("DELETED", id));
        });
        return candidate;
    }

    @Transactional(readOnly = true)
    public long countByFuelConsumptionGreaterThan(Double value) {
        return repository.countByFuelConsumptionGreaterThan(value);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findByType(VehicleType type) {
        return repository.findAllByTypeOrderByIdAsc(type);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findByEnginePowerRange(Integer min, Integer max) {
        if (min > max) {
            throw new IllegalArgumentException("Минимальная мощность не может быть больше максимальной");
        }
        return repository.findAllByEnginePowerBetweenOrderByEnginePowerAsc(min, max);
    }
}

package ru.itmo.vehiclelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.itmo.vehiclelab.domain.FuelType;
import ru.itmo.vehiclelab.domain.Vehicle;
import ru.itmo.vehiclelab.domain.VehicleType;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>, JpaSpecificationExecutor<Vehicle> {

    List<Vehicle> findAllByCoordinatesId(Integer coordinatesId);

    long countByCoordinatesId(Integer coordinatesId);

    long deleteByFuelType(FuelType fuelType);

    Optional<Vehicle> findFirstByFuelConsumptionOrderByIdAsc(Double fuelConsumption);

    long countByFuelConsumptionGreaterThan(Double fuelConsumption);

    List<Vehicle> findAllByTypeOrderByIdAsc(VehicleType type);

    List<Vehicle> findAllByEnginePowerBetweenOrderByEnginePowerAsc(Integer min, Integer max);
}

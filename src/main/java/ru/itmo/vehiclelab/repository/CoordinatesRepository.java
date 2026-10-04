package ru.itmo.vehiclelab.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.vehiclelab.domain.Coordinates;
public interface CoordinatesRepository extends JpaRepository<Coordinates, Integer> {}

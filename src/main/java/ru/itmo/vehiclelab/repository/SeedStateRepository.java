package ru.itmo.vehiclelab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.vehiclelab.domain.SeedState;

public interface SeedStateRepository extends JpaRepository<SeedState, String> {}

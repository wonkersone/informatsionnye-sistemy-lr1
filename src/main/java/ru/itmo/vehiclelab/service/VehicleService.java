package ru.itmo.vehiclelab.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.itmo.vehiclelab.domain.Coordinates;
import ru.itmo.vehiclelab.domain.FuelType;
import ru.itmo.vehiclelab.domain.Vehicle;
import ru.itmo.vehiclelab.domain.VehicleType;
import ru.itmo.vehiclelab.repository.VehicleRepository;
import ru.itmo.vehiclelab.web.VehicleForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class VehicleService {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "name", "creationDate", "type", "enginePower", "numberOfWheels",
            "capacity", "distanceTravelled", "fuelConsumption", "fuelType"
    );

    private final VehicleRepository repository;
    private final CoordinatesService coordinatesService;
    private final ApplicationEventPublisher eventPublisher;

    public VehicleService(VehicleRepository repository, ApplicationEventPublisher eventPublisher, CoordinatesService coordinatesService) {
        this.repository = repository;
        this.coordinatesService = coordinatesService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public Page<Vehicle> findPage(String name, VehicleType type, FuelType fuelType,
                                  int page, int size, String sortField, Sort.Direction direction) {
        String safeSort = SORTABLE_FIELDS.contains(sortField) ? sortField : "id";
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 5), 100);
        Specification<Vehicle> specification = filters(name, type, fuelType);
        return repository.findAll(specification, PageRequest.of(safePage, safeSize, Sort.by(direction, safeSort)));
    }

    private Specification<Vehicle> filters(String name, VehicleType type, FuelType fuelType) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(name)) {
                predicates.add(builder.equal(root.get("name"), name.trim()));
            }
            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }
            if (fuelType != null) {
                predicates.add(builder.equal(root.get("fuelType"), fuelType));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Transactional(readOnly = true)
    public Vehicle get(Integer id) {
        return repository.findById(id).orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @Transactional
    public Vehicle create(VehicleForm form) {
        Vehicle vehicle = new Vehicle(form.getName().trim(), coordinates(form), form.getType(),
                form.getEnginePower(), form.getNumberOfWheels(), form.getCapacity(),
                form.getDistanceTravelled(), form.getFuelConsumption(), form.getFuelType());
        Vehicle saved = repository.save(vehicle);
        eventPublisher.publishEvent(new VehicleChangedEvent("CREATED", saved.getId()));
        return saved;
    }

    @Transactional
    public Vehicle update(Integer id, VehicleForm form) {
        Vehicle vehicle = get(id);
        vehicle.update(form.getName().trim(), coordinates(form), form.getType(), form.getEnginePower(),
                form.getNumberOfWheels(), form.getCapacity(), form.getDistanceTravelled(),
                form.getFuelConsumption(), form.getFuelType());
        eventPublisher.publishEvent(new VehicleChangedEvent("UPDATED", vehicle.getId()));
        return vehicle;
    }

    @Transactional
    public void delete(Integer id) {
        Vehicle vehicle = get(id);
        repository.delete(vehicle);
        eventPublisher.publishEvent(new VehicleChangedEvent("DELETED", id));
    }

    @Transactional(readOnly = true)
    public VehicleForm toForm(Integer id) {
        Vehicle vehicle = get(id);
        VehicleForm form = new VehicleForm();
        form.setName(vehicle.getName());
        form.setCoordinatesId(vehicle.getCoordinates().getId());
        form.setCoordinateX(vehicle.getCoordinates().getX());
        form.setCoordinateY(vehicle.getCoordinates().getY());
        form.setType(vehicle.getType());
        form.setEnginePower(vehicle.getEnginePower());
        form.setNumberOfWheels(vehicle.getNumberOfWheels());
        form.setCapacity(vehicle.getCapacity());
        form.setDistanceTravelled(vehicle.getDistanceTravelled());
        form.setFuelConsumption(vehicle.getFuelConsumption());
        form.setFuelType(vehicle.getFuelType());
        return form;
    }

    private Coordinates coordinates(VehicleForm form) {
        return form.getCoordinatesId() == null
                ? coordinatesService.create(form.getCoordinateX(), form.getCoordinateY())
                : coordinatesService.get(form.getCoordinatesId());
    }
}

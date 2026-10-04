package ru.itmo.vehiclelab.service;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import ru.itmo.vehiclelab.domain.Coordinates;
import ru.itmo.vehiclelab.repository.CoordinatesRepository;
import ru.itmo.vehiclelab.repository.VehicleRepository;
@Service
@Transactional
public class CoordinatesService {
    private final CoordinatesRepository repository;
    private final VehicleRepository vehicles;
    private final ApplicationEventPublisher events;
    public CoordinatesService(CoordinatesRepository repository, VehicleRepository vehicles, ApplicationEventPublisher events) {
        this.repository = repository; this.vehicles = vehicles; this.events = events;
    }
    @Transactional(readOnly=true)
    public List<Coordinates> all() { return repository.findAll(org.springframework.data.domain.Sort.by("id")); }
    @Transactional(readOnly=true)
    public Coordinates get(Integer id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Координаты #" + id + " не найдены")); }
    public Coordinates create(Double x, Float y) {
        validate(x, y);
        Coordinates saved = repository.save(new Coordinates(x,y));
        events.publishEvent(new VehicleChangedEvent("COORDINATES_CREATED", null));
        return saved;
    }
    public void update(Integer id, Double x, Float y) {
        validate(x,y); get(id).update(x,y);
        events.publishEvent(new VehicleChangedEvent("COORDINATES_UPDATED", null));
    }
    public void delete(Integer id, Integer replacementId) {
        Coordinates source = get(id);
        var linked = vehicles.findAllByCoordinatesId(id);
        if (!linked.isEmpty()) {
            if (replacementId == null || id.equals(replacementId))
                throw new IllegalArgumentException("Выберите другие координаты для связанных транспортных средств");
            Coordinates replacement = get(replacementId);
            linked.forEach(vehicle -> vehicle.setCoordinates(replacement));
            vehicles.flush();
        }
        repository.delete(source);
        events.publishEvent(new VehicleChangedEvent("COORDINATES_DELETED", null));
    }
    @Transactional(readOnly=true)
    public long linkedCount(Integer id) { return vehicles.countByCoordinatesId(id); }
    private void validate(Double x, Float y) {
        if (x == null || y == null || !Double.isFinite(x) || !Float.isFinite(y) || y > 408)
            throw new IllegalArgumentException("X и Y должны быть конечными числами; Y не больше 408");
    }
}

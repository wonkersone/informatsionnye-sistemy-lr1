package ru.itmo.vehiclelab.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "vehicles")
@Check(constraints = "engine_power > 0 AND number_of_wheels > 0 AND capacity > 0 " +
        "AND distance_travelled > 0 AND fuel_consumption > 0 AND id > 0 AND length(trim(name)) > 0 " +
        "AND capacity <= 1.7976931348623157E308 AND fuel_consumption <= 1.7976931348623157E308")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive
    private Integer id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @Valid
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @NotNull
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private VehicleType type;

    @Positive
    @Column(name = "engine_power", nullable = false)
    private int enginePower;

    @NotNull
    @Positive
    @Column(name = "number_of_wheels", nullable = false)
    private Integer numberOfWheels;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double capacity;

    @Positive
    @Column(name = "distance_travelled", nullable = false)
    private int distanceTravelled;

    @Positive
    @Column(name = "fuel_consumption", nullable = false)
    private double fuelConsumption;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 16)
    private FuelType fuelType;

    protected Vehicle() {
    }

    @PrePersist
    private void initializeCreationDate() {
        if (creationDate == null) {
            creationDate = new Date();
        }
    }

    public Vehicle(String name, Coordinates coordinates, VehicleType type, int enginePower,
                   Integer numberOfWheels, Double capacity, int distanceTravelled,
                   double fuelConsumption, FuelType fuelType) {
        update(name, coordinates, type, enginePower, numberOfWheels, capacity,
                distanceTravelled, fuelConsumption, fuelType);
    }

    public void update(String name, Coordinates coordinates, VehicleType type, int enginePower,
                       Integer numberOfWheels, Double capacity, int distanceTravelled,
                       double fuelConsumption, FuelType fuelType) {
        this.name = name;
        this.coordinates = coordinates;
        this.type = type;
        this.enginePower = enginePower;
        this.numberOfWheels = numberOfWheels;
        this.capacity = capacity;
        this.distanceTravelled = distanceTravelled;
        this.fuelConsumption = fuelConsumption;
        this.fuelType = fuelType;
    }

    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    @AssertTrue(message = "Вместимость и расход должны быть конечными числами")
    public boolean isFinite() { return (capacity == null || Double.isFinite(capacity)) && Double.isFinite(fuelConsumption); }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Coordinates getCoordinates() { return coordinates; }
    public Date getCreationDate() { return creationDate; }
    public VehicleType getType() { return type; }
    public int getEnginePower() { return enginePower; }
    public Integer getNumberOfWheels() { return numberOfWheels; }
    public Double getCapacity() { return capacity; }
    public int getDistanceTravelled() { return distanceTravelled; }
    public double getFuelConsumption() { return fuelConsumption; }
    public FuelType getFuelType() { return fuelType; }
}

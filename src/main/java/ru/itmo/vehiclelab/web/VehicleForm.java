package ru.itmo.vehiclelab.web;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;
import ru.itmo.vehiclelab.domain.FuelType;
import ru.itmo.vehiclelab.domain.VehicleType;

public class VehicleForm {

    @NotBlank(message = "Введите название транспортного средства")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @Positive(message = "ID координат должен быть больше 0")
    private Integer coordinatesId;
    public Integer getCoordinatesId() { return coordinatesId; }
    public void setCoordinatesId(Integer value) { coordinatesId = value; }

    @AssertTrue(message = "Введите конечные числовые значения")
    public boolean isFinite() {
        return (coordinateX == null || Double.isFinite(coordinateX)) &&
            (coordinateY == null || Float.isFinite(coordinateY)) &&
            (capacity == null || Double.isFinite(capacity)) &&
            (fuelConsumption == null || Double.isFinite(fuelConsumption));
    }

    private Double coordinateX;

    @DecimalMax(value = "408", message = "Координата Y не должна превышать 408")
    private Float coordinateY;

    @NotNull(message = "Выберите тип транспортного средства")
    private VehicleType type;

    @NotNull(message = "Введите мощность двигателя")
    @Positive(message = "Мощность двигателя должна быть больше 0")
    private Integer enginePower;

    @NotNull(message = "Введите количество колёс")
    @Positive(message = "Количество колёс должно быть больше 0")
    private Integer numberOfWheels;

    @NotNull(message = "Введите вместимость")
    @Positive(message = "Вместимость должна быть больше 0")
    private Double capacity;

    @NotNull(message = "Введите пройденное расстояние")
    @Positive(message = "Пройденное расстояние должно быть больше 0")
    private Integer distanceTravelled;

    @NotNull(message = "Введите расход топлива")
    @Positive(message = "Расход топлива должен быть больше 0")
    private Double fuelConsumption;

    @NotNull(message = "Выберите тип топлива")
    private FuelType fuelType;

    @AssertTrue(message = "Выберите координаты или введите X и Y")
    public boolean isCoordinatesProvided() { return coordinatesId != null || (coordinateX != null && coordinateY != null); }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getCoordinateX() { return coordinateX; }
    public void setCoordinateX(Double coordinateX) { this.coordinateX = coordinateX; }
    public Float getCoordinateY() { return coordinateY; }
    public void setCoordinateY(Float coordinateY) { this.coordinateY = coordinateY; }
    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }
    public Integer getEnginePower() { return enginePower; }
    public void setEnginePower(Integer enginePower) { this.enginePower = enginePower; }
    public Integer getNumberOfWheels() { return numberOfWheels; }
    public void setNumberOfWheels(Integer numberOfWheels) { this.numberOfWheels = numberOfWheels; }
    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }
    public Integer getDistanceTravelled() { return distanceTravelled; }
    public void setDistanceTravelled(Integer distanceTravelled) { this.distanceTravelled = distanceTravelled; }
    public Double getFuelConsumption() { return fuelConsumption; }
    public void setFuelConsumption(Double fuelConsumption) { this.fuelConsumption = fuelConsumption; }
    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
}

package ru.itmo.vehiclelab.service;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(Integer id) {
        super("Транспортное средство с ID " + id + " не найдено");
    }
}

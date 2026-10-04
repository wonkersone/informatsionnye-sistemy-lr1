package ru.itmo.vehiclelab.service;

public record VehicleChangedEvent(String operation, Integer vehicleId) {
}

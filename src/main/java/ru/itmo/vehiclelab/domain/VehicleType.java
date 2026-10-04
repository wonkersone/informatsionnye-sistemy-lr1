package ru.itmo.vehiclelab.domain;

public enum VehicleType {
    PLANE("Самолёт"),
    BOAT("Лодка"),
    SHIP("Корабль");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

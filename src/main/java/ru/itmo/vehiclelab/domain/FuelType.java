package ru.itmo.vehiclelab.domain;

public enum FuelType {
    DIESEL("Дизель"),
    ALCOHOL("Спирт"),
    MANPOWER("Мускульная сила");

    private final String displayName;

    FuelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

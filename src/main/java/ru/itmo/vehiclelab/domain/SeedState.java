package ru.itmo.vehiclelab.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "app_seed_state")
public class SeedState {
    @Id
    @Column(length = 64)
    private String name;

    protected SeedState() {}
    public SeedState(String name) { this.name = name; }
}

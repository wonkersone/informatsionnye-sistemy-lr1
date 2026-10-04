CREATE TABLE vehicles (
    id                  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    coordinate_x        DOUBLE PRECISION NOT NULL,
    coordinate_y        REAL NOT NULL,
    creation_date       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type                VARCHAR(16) NOT NULL,
    engine_power        INTEGER NOT NULL,
    number_of_wheels    INTEGER NOT NULL,
    capacity            DOUBLE PRECISION NOT NULL,
    distance_travelled  INTEGER NOT NULL,
    fuel_consumption    DOUBLE PRECISION NOT NULL,
    fuel_type           VARCHAR(16) NOT NULL,

    CONSTRAINT ck_vehicle_id_positive CHECK (id > 0),
    CONSTRAINT ck_vehicle_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT ck_coordinate_y_max CHECK (coordinate_y <= 408),
    CONSTRAINT ck_vehicle_type CHECK (type IN ('PLANE', 'BOAT', 'SHIP')),
    CONSTRAINT ck_engine_power_positive CHECK (engine_power > 0),
    CONSTRAINT ck_wheels_positive CHECK (number_of_wheels > 0),
    CONSTRAINT ck_capacity_positive CHECK (capacity > 0),
    CONSTRAINT ck_distance_positive CHECK (distance_travelled > 0),
    CONSTRAINT ck_consumption_positive CHECK (fuel_consumption > 0),
    CONSTRAINT ck_fuel_type CHECK (fuel_type IN ('DIESEL', 'ALCOHOL', 'MANPOWER'))
);

CREATE INDEX idx_vehicle_name ON vehicles (name);
CREATE INDEX idx_vehicle_type ON vehicles (type);
CREATE INDEX idx_vehicle_fuel_type ON vehicles (fuel_type);
CREATE INDEX idx_vehicle_engine_power ON vehicles (engine_power);

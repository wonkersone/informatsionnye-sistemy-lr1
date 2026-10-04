package ru.itmo.vehiclelab.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.AssertTrue;
import org.hibernate.annotations.Check;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "coordinates")
@Check(constraints = "id > 0 AND coordinate_x BETWEEN -1.7976931348623157E308 AND 1.7976931348623157E308 AND coordinate_y BETWEEN -3.4028235E38 AND 408")
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() { return id; }

    public void update(Double x, Float y) { this.x = x; this.y = y; }

    @AssertTrue(message = "Координаты должны быть конечными числами")
    public boolean isFinite() { return (x == null || Double.isFinite(x)) && (y == null || Float.isFinite(y)); }

    @NotNull
    @Column(name = "coordinate_x", nullable = false)
    private Double x;

    @NotNull
    @DecimalMax(value = "408", message = "Координата Y не должна превышать 408")
    @Column(name = "coordinate_y", nullable = false)
    private Float y;

    protected Coordinates() {
    }

    public Coordinates(Double x, Float y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Float getY() {
        return y;
    }
}

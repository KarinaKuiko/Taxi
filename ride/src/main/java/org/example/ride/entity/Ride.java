package org.example.ride.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.ride.entity.enumeration.RideStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "rides")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Ride extends AuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long driverId;

    private Long passengerId;

    private String addressFrom;

    private String addressTo;

    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    @Column(precision = 6, scale = 2)
    private BigDecimal cost;
}

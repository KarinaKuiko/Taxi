package org.example.ride.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "rides")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ride extends AuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rides_id_seq")
    @SequenceGenerator(name = "rides_id_seq", sequenceName = "rides_id_seq", allocationSize = 1)
    private Long id;

    private Long driverId;

    private Long passengerId;

    private String addressFrom;

    private String addressTo;

    @Enumerated(EnumType.STRING)
    private DriverRideStatus driverRideStatus;

    @Enumerated(EnumType.STRING)
    private PassengerRideStatus passengerRideStatus;

    @Column(precision = 6, scale = 2)
    private BigDecimal cost;
}

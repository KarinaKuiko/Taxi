package org.example.rating.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.rating.entity.enumeration.UserType;

@Entity
@Table(name = "driver_rates")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class DriverRate extends Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rideId;

    private String comment;

    private Integer rating;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}

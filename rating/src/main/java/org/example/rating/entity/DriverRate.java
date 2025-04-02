package org.example.rating.entity;

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
import org.example.rating.entity.enumeration.UserType;

@Entity
@Table(name = "driver_rates")
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverRate extends Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_rates_id_seq")
    @SequenceGenerator(name = "driver_rates_id_seq", sequenceName = "driver_rates_id_seq", allocationSize = 1)
    private Long id;

    private Long rideId;

    private String comment;

    private Integer rating;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}

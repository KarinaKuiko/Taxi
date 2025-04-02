package org.example.passenger.entity;

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
import org.example.passenger.entity.enumeration.Gender;

@Entity
@Table(name = "passengers")
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Passenger extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passengers_id_seq")
    @SequenceGenerator(name = "passengers_id_seq", sequenceName = "passengers_id_seq", allocationSize = 1)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Double rating;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String imageUrl;
}

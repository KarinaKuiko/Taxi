package org.example.driver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.driver.entity.enumeration.Gender;

@Entity
@Table(name = "drivers")
@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Driver extends AuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne
    private Car car;

}

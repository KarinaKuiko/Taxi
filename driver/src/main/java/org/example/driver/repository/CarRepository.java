package org.example.driver.repository;

import org.example.driver.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByIdAndIsDeletedFalse(Long id);

    Page<Car> findByIsDeletedFalse(Pageable pageable);

    Optional<Car> findByNumberAndIsDeletedFalse(String number);
}

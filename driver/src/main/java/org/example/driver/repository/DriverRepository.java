package org.example.driver.repository;

import org.example.driver.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.DataTruncation;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByIdAndIsDeletedFalse(Long id);

    Page<Driver> findByIsDeletedFalse(Pageable pageable);

    Optional<Driver> findByEmailAndIsDeletedFalse(String email);

    List<Driver> findByCarIdAndIsDeletedFalse(Long id);
}

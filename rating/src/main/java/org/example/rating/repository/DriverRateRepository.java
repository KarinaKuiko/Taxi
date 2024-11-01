package org.example.rating.repository;

import org.example.rating.entity.DriverRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRateRepository extends JpaRepository<DriverRate, Long> {
    List<DriverRate> findByUserId(Long id);
}

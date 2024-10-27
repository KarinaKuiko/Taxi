package org.example.rating.repository;

import org.example.rating.entity.PassengerRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PassengerRateRepository extends JpaRepository<PassengerRate, Long> {
    List<PassengerRate> findByUserId(Long id);

}

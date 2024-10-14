package org.example.ride.repository;

import org.example.ride.entity.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Page<Ride> findByPassengerId(Long id, Pageable pageable);

    Page<Ride> findByDriverId(Long id, Pageable pageable);
}
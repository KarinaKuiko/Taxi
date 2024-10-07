package org.example.passenger.repository;

import org.example.passenger.entity.Passenger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,Long> {

    Page<Passenger> findByIsDeletedFalse(Pageable pageable);

    Optional<Passenger> findByIdAndIsDeletedFalse(Long id);

    Optional<Passenger> findByEmailAndIsDeletedFalse(String email);
}

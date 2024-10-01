package org.example.driver.service;

import lombok.RequiredArgsConstructor;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.entity.Car;
import org.example.driver.exception.CarIsDeletedException;
import org.example.driver.mapper.DriverMapper;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarRepository carRepository;

    public DriverReadDto create(DriverCreateEditDto driverDto) {

        return Optional.of(driverDto)
                .map(driverMapper::toDriver)
                .map(driver -> {
                    Optional<Car> car = carRepository.findByIdAndIsDeletedIsFalse(driverDto.car_id());
                    if (car.isEmpty()) {
                        throw new CarIsDeletedException("Car has been deleted", HttpStatus.NOT_FOUND);
                    }
                    driver.setCar(car.get());
                    return driver;
                })
                .map(driverRepository::save)
                .map(driverMapper::toReadDto)
                .orElseThrow();
    }

    public Optional<DriverReadDto> update(Long id, DriverCreateEditDto driverDto) {
        return driverRepository.findById(id)
                .map(driver -> {
                    driverMapper.map(driver, driverDto);
                    Optional<Car> car = carRepository.findByIdAndIsDeletedIsFalse(driverDto.car_id());
                    if (car.isEmpty()) {
                        throw new CarIsDeletedException("Car has been deleted", HttpStatus.NOT_FOUND);
                    }
                    driver.setCar(car.get());
                    return driver;
                })
                .map(driverRepository::save)
                .map(driverMapper::toReadDto);
    }

    public boolean safeDelete(Long id) {
        return driverRepository.findByIdAndIsDeletedIsFalse(id)
                .map(driver -> {
                    driver.setDeleted(true);
                    driverRepository.save(driver);
                    return true;
                })
                .orElse(false);
    }

    public List<DriverReadDto> findAll() {
        return driverRepository.findAll()
                .stream()
                .map(driverMapper::toReadDto)
                .toList();
    }

    public Optional<DriverReadDto> findById(Long id) {
        return driverRepository.findById(id)
                .map(driverMapper::toReadDto);
    }
}

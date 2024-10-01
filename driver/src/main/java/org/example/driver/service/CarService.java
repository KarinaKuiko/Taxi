package org.example.driver.service;

import lombok.RequiredArgsConstructor;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.mapper.CarMapper;
import org.example.driver.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    public CarReadDto create(CarCreateEditDto carDto) {
        return Optional.of(carDto)
                .map(carMapper::toCar)
                .map(carRepository::save)
                .map(carMapper::toReadDto)
                .orElseThrow();
    }

    public Optional<CarReadDto> update(Long id, CarCreateEditDto carDto) {
        return carRepository.findById(id)
                .map(car -> {
                    carMapper.map(car, carDto);
                    return car;
                })
                .map(carRepository::save)
                .map(carMapper::toReadDto);
    }

    public boolean safeDelete(Long id) {
        return carRepository.findByIdAndIsDeletedIsFalse(id)
                .map(car -> {
                    car.setDeleted(true);
                    carRepository.save(car);
                    return true;
                })
                .orElse(false);
    }

    public List<CarReadDto> findAll() {
        return carRepository.findAll()
                .stream()
                .map(carMapper::toReadDto)
                .toList();
    }

    public Optional<CarReadDto> findById(Long id) {
        return carRepository.findById(id)
                .map(carMapper::toReadDto);
    }
}

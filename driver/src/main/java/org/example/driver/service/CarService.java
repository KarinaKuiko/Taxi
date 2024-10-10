package org.example.driver.service;

import lombok.RequiredArgsConstructor;
import org.example.driver.constants.AppConstants;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.entity.Car;
import org.example.driver.exception.car.CarNotFoundException;
import org.example.driver.exception.car.DuplicatedCarNumberException;
import org.example.driver.mapper.CarMapper;
import org.example.driver.repository.CarRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final MessageSource messageSource;

    @Transactional
    public CarReadDto create(CarCreateEditDto carDto) {
        carRepository.findByNumberAndIsDeletedFalse(carDto.number())
                .ifPresent(car -> {
                    throw new DuplicatedCarNumberException(messageSource.getMessage(
                            AppConstants.CAR_DUPLICATED_NUMBER,
                            new Object[]{carDto.number()},
                            LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
                });

        Car car = carMapper.toCar(carDto);

        return carMapper.toReadDto(carRepository.save(car));
    }

    @Transactional
    public CarReadDto update(Long id, CarCreateEditDto carDto) {
        carRepository.findByNumberAndIsDeletedFalse(carDto.number())
                .ifPresent(car -> {
                    if (!car.getId().equals(id)) {
                        throw new DuplicatedCarNumberException(messageSource.getMessage(
                                AppConstants.CAR_DUPLICATED_NUMBER,
                                new Object[]{carDto.number()},
                                LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
                    }
                });

        return carRepository.findByIdAndIsDeletedFalse(id)
                .map(car -> {
                    carMapper.map(car, carDto);
                    return car;
                })
                .map(carRepository::save)
                .map(carMapper::toReadDto)
                .orElseThrow(() -> new CarNotFoundException(messageSource.getMessage(
                        AppConstants.CAR_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void safeDelete(Long id) {
        carRepository.findByIdAndIsDeletedFalse(id)
                .map(car -> {
                    car.setDeleted(true);
                    carRepository.save(car);
                    return car;
                })
                .orElseThrow(() -> new CarNotFoundException(messageSource.getMessage(
                                                            AppConstants.CAR_NOT_FOUND,
                                                            new Object[]{id},
                                                            LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND));

    }

    public Page<CarReadDto> findAll(Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);
        return carRepository.findByIsDeletedFalse(request)
                .map(carMapper::toReadDto);

    }

    public Page<CarReadDto> findAllWithDeleted(Integer page, Integer limit) {
        PageRequest request = PageRequest.of(page, limit);
        return carRepository.findAll(request)
                .map(carMapper::toReadDto);
    }

    public CarReadDto findById(Long id) {
        return carRepository.findByIdAndIsDeletedFalse(id)
                .map(carMapper::toReadDto)
                .orElseThrow(() -> new CarNotFoundException(messageSource.getMessage(
                        AppConstants.CAR_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND
                ));
    }
}
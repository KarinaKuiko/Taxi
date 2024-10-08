package org.example.driver.service;

import lombok.RequiredArgsConstructor;
import org.example.driver.constants.AppConstants;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;
import org.example.driver.exception.car.CarNotFoundException;
import org.example.driver.exception.driver.DriverNotFoundException;
import org.example.driver.exception.driver.DuplicatedDriverEmailException;
import org.example.driver.mapper.DriverMapper;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    private final CarRepository carRepository;
    private final MessageSource messageSource;

    @Transactional
    public DriverReadDto create(DriverCreateEditDto driverDto) {
        driverRepository.findByEmailAndIsDeletedFalse(driverDto.email())
                .ifPresent(driver -> {
                    throw new DuplicatedDriverEmailException(messageSource.getMessage(
                            AppConstants.DRIVER_DUPLICATED_EMAIL,
                            new Object[]{driverDto.email()},
                            LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
                });

        Driver driver = driverMapper.toDriver(driverDto);

        Car car = carRepository.findByIdAndIsDeletedFalse(driverDto.carId())
                .orElseThrow(() -> new CarNotFoundException(messageSource.getMessage(
                        AppConstants.CAR_NOT_FOUND,
                        new Object[]{driverDto.carId()},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND));

        driver.setCar(car);

        return driverMapper.toReadDto(driverRepository.save(driver));

    }

    @Transactional
    public DriverReadDto update(Long id, DriverCreateEditDto driverDto) {
        driverRepository.findByEmailAndIsDeletedFalse(driverDto.email())
                .ifPresent(driver -> {
                    throw new DuplicatedDriverEmailException(messageSource.getMessage(
                            AppConstants.DRIVER_DUPLICATED_EMAIL,
                            new Object[]{driverDto.email()},
                            LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
                });

        return driverRepository.findByIdAndIsDeletedFalse(id)
                .map(driver -> {
                    driverMapper.map(driver, driverDto);
                    Car car = carRepository.findByIdAndIsDeletedFalse(driverDto.carId())
                            .orElseThrow(() -> new CarNotFoundException(messageSource.getMessage(
                                    AppConstants.CAR_NOT_FOUND,
                                    new Object[]{driverDto.carId()},
                                    LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND));
                    driver.setCar(car);
                    return driver;
                })
                .map(driverRepository::save)
                .map(driverMapper::toReadDto)
                .orElseThrow(() -> new DriverNotFoundException(messageSource.getMessage(
                        AppConstants.DRIVER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void safeDelete(Long id) {
        driverRepository.findByIdAndIsDeletedFalse(id)
                .map(driver -> {
                    driver.setDeleted(true);
                    driverRepository.save(driver);
                    return driver;
                })
                .orElseThrow(() -> new DriverNotFoundException(messageSource.getMessage(
                        AppConstants.DRIVER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND));
    }

    public Page<DriverReadDto> findAll(Integer page, Integer limit) {
        PageRequest request = PageRequest.of(page, limit);
        return driverRepository.findByIsDeletedFalse(request)
                .map(driverMapper::toReadDto);
    }

    public Page<DriverReadDto> findAllWithDeleted(Integer page, Integer limit) {
        PageRequest request = PageRequest.of(page, limit);
        return driverRepository.findAll(request)
                .map(driverMapper::toReadDto);
    }

    public DriverReadDto findById(Long id) {
        return driverRepository.findByIdAndIsDeletedFalse(id)
                .map(driverMapper::toReadDto)
                .orElseThrow(() -> new DriverNotFoundException(messageSource.getMessage(
                        AppConstants.DRIVER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND
                ));
    }
}

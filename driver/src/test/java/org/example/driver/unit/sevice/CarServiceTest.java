package org.example.driver.unit.sevice;

import org.example.driver.constants.ExceptionConstants;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;
import org.example.driver.exception.car.CarNotFoundException;
import org.example.driver.exception.car.DuplicatedCarNumberException;
import org.example.driver.mapper.CarMapper;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
import org.example.driver.service.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.driver.util.DataUtil.DEFAULT_ID;
import static org.example.driver.util.DataUtil.LIMIT_VALUE;
import static org.example.driver.util.DataUtil.PAGE_VALUE;
import static org.example.driver.util.DataUtil.getCar;
import static org.example.driver.util.DataUtil.getCarCreateEditDto;
import static org.example.driver.util.DataUtil.getCarReadDto;
import static org.example.driver.util.DataUtil.getDriver;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private CarService carService;

    private Car defaultCar = getCar().build();
    private CarCreateEditDto createCar = getCarCreateEditDto();
    private CarReadDto readCar = getCarReadDto();
    private Driver driver = getDriver().build();

    @Test
    void create_whenCarNumberIsNotDuplicated_thenReturnCarReadDto() {
        when(carRepository.findByNumberAndIsDeletedFalse(createCar.number()))
                .thenReturn(Optional.empty());
        when(carRepository.save(defaultCar)).thenReturn(defaultCar);
        when(carMapper.toCar(createCar)).thenReturn(defaultCar);
        when(carMapper.toReadDto(defaultCar)).thenReturn(readCar);

        assertThat(carService.create(createCar)).isNotNull();
        verify(carRepository).findByNumberAndIsDeletedFalse(createCar.number());
        verify(carRepository).save(defaultCar);
        verify(carMapper).toCar(createCar);
        verify(carMapper).toReadDto(defaultCar);
    }

    @Test
    void create_whenCarNumberIsDuplicated_thenThrowDuplicatedCarNumberException() {
        when(carRepository.findByNumberAndIsDeletedFalse(createCar.number()))
                .thenReturn(Optional.of(defaultCar));
        when(messageSource.getMessage(
                ExceptionConstants.CAR_DUPLICATED_NUMBER,
                new Object[]{createCar.number()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.CAR_DUPLICATED_NUMBER);

        DuplicatedCarNumberException exception = assertThrows(DuplicatedCarNumberException.class,
                () -> carService.create(createCar));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.CAR_DUPLICATED_NUMBER);
        verify(carRepository).findByNumberAndIsDeletedFalse(createCar.number());
        verify(messageSource).getMessage(
                ExceptionConstants.CAR_DUPLICATED_NUMBER,
                new Object[]{createCar.number()},
                LocaleContextHolder.getLocale());
        verify(carRepository, never()).save(any());
        verify(carMapper, never()).toCar(any());
        verify(carMapper, never()).toReadDto(any());
    }

    @Test
    void update_whenCarNumberIsNotDuplicatedAndCarIsFound_thenReturnCarReadDto() {
        when(carRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultCar));
        when(carRepository.findByNumberAndIsDeletedFalse(createCar.number()))
                .thenReturn(Optional.empty());
        when(carRepository.save(defaultCar)).thenReturn(defaultCar);
        when(carMapper.toReadDto(defaultCar)).thenReturn(readCar);


        assertThat(carService.update(DEFAULT_ID, createCar)).isNotNull();
        verify(carRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(carMapper).map(defaultCar, createCar);
        verify(carRepository).findByNumberAndIsDeletedFalse(createCar.number());
        verify(carRepository).save(defaultCar);
        verify(carMapper).toReadDto(defaultCar);
    }

    @Test
    void update_whenCarNumberIsDuplicatedAndDifferentIds_thenThrowDuplicatedCarNumberException() {
        when(carRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(defaultCar));
        when(carRepository.findByNumberAndIsDeletedFalse(createCar.number()))
                .thenReturn(Optional.of(defaultCar));
        when(messageSource.getMessage(
                ExceptionConstants.CAR_DUPLICATED_NUMBER,
                new Object[]{createCar.number()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.CAR_DUPLICATED_NUMBER);

        DuplicatedCarNumberException exception = assertThrows(DuplicatedCarNumberException.class,
                () -> carService.update(2L, createCar));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.CAR_DUPLICATED_NUMBER);
        verify(carRepository).findByIdAndIsDeletedFalse(2L);
        verify(carRepository).findByNumberAndIsDeletedFalse(createCar.number());
        verify(messageSource).getMessage(
                ExceptionConstants.CAR_DUPLICATED_NUMBER,
                new Object[]{createCar.number()},
                LocaleContextHolder.getLocale());
        verify(carMapper, never()).map(any(), any());
        verify(carRepository, never()).save(any());
        verify(carMapper, never()).toReadDto(any());
    }

    @Test
    void update_whenCarNumberIsDuplicatedAndSameIds_thenReturnCarReadDto() {
        when(carRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultCar));
        when(carRepository.findByNumberAndIsDeletedFalse(createCar.number()))
                .thenReturn(Optional.of(defaultCar));
        when(carRepository.save(defaultCar)).thenReturn(defaultCar);
        when(carMapper.toReadDto(defaultCar)).thenReturn(readCar);

        assertThat(carService.update(DEFAULT_ID, createCar)).isNotNull();
        verify(carRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(carRepository).findByNumberAndIsDeletedFalse(createCar.number());
        verify(carMapper).map(defaultCar, createCar);
        verify(carRepository).save(defaultCar);
        verify(carMapper).toReadDto(defaultCar);
    }

    @Test
    void update_whenCarNotFound_thenThrowCarNotFoundException() {
        when(carRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{2L},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.CAR_NOT_FOUND);

        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> carService.update(2L, createCar));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.CAR_NOT_FOUND);
        verify(carRepository).findByIdAndIsDeletedFalse(2L);
        verify(messageSource).getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{2L},
                LocaleContextHolder.getLocale());
        verify(carRepository, never()).findByNumberAndIsDeletedFalse(any());
        verify(carMapper, never()).map(any(), any());
        verify(carRepository, never()).save(any());
        verify(carMapper, never()).toReadDto(any());
    }

    @Test
    void safeDelete_whenCarIsFoundWithDrivers_thenUnsetDrivers() {
        when(carRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultCar));
        when(carRepository.save(defaultCar)).thenReturn(defaultCar);
        when(driverRepository.save(driver)).thenReturn(driver);
        when(driverRepository.findByCarIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(List.of(driver));

        carService.safeDelete(DEFAULT_ID);

        assertThat(defaultCar.isDeleted()).isTrue();
        assertThat(driver.getCar()).isNull();
        verify(carRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(carRepository).save(defaultCar);
        verify(driverRepository).findByCarIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository).save(driver);
    }

    @Test
    void safeDelete_whenCarIsFoundWithoutDrivers_thenMarkAsDeleted() {
        when(carRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultCar));
        when(carRepository.save(defaultCar)).thenReturn(defaultCar);
        when(driverRepository.findByCarIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(List.of());

        carService.safeDelete(DEFAULT_ID);

        assertThat(defaultCar.isDeleted()).isTrue();
        verify(carRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(carRepository).save(defaultCar);
        verify(driverRepository).findByCarIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository, never()).save(any());
    }

    @Test
    void safeDelete_whenCarIsNotFound_thenThrowCarNotFoundException() {
        when(carRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.CAR_NOT_FOUND);

        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> carService.safeDelete(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.CAR_NOT_FOUND);
        assertThat(defaultCar.isDeleted()).isFalse();
        verify(carRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(messageSource).getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(carRepository, never()).save(any());
        verify(driverRepository, never()).save(any());
        verify(driverRepository, never()).findByCarIdAndIsDeletedFalse(DEFAULT_ID);
    }

    @Test
    void findAll_thenReturnPageCarReadDto() {
        PageRequest request = PageRequest.of(PAGE_VALUE, LIMIT_VALUE);

        when(carRepository.findByIsDeletedFalse(request)).thenReturn(
                new PageImpl<>(List.of(defaultCar), request, 1));
        when(carMapper.toReadDto(defaultCar)).thenReturn(readCar);

        Page<CarReadDto> result = carService.findAll(PAGE_VALUE, LIMIT_VALUE);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(carRepository).findByIsDeletedFalse(request);
        verify(carMapper).toReadDto(defaultCar);
    }

    @Test
    void findAllWithDeleted_thenReturnPageCarReadDto() {
        PageRequest request = PageRequest.of(PAGE_VALUE, LIMIT_VALUE);

        when(carRepository.findAll(request)).thenReturn(new PageImpl<>(List.of(defaultCar), request, 1));
        when(carMapper.toReadDto(defaultCar)).thenReturn(readCar);

        Page<CarReadDto> result = carService.findAllWithDeleted(PAGE_VALUE, LIMIT_VALUE);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(carRepository).findAll(request);
        verify(carMapper).toReadDto(defaultCar);
    }

    @Test
    void findById_whenCarIsFound_thenReturnCarReadDto() {
        when(carRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultCar));
        when(carMapper.toReadDto(defaultCar)).thenReturn(readCar);

        assertThat(carService.findById(DEFAULT_ID)).isNotNull();
        verify(carRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(carMapper).toReadDto(defaultCar);
    }

    @Test
    void findById_whenCarIsNotFound_thenThrowCarNotFoundException() {
        when(carRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.CAR_NOT_FOUND);

        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> carService.findById(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.CAR_NOT_FOUND);
        verify(messageSource).getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(carRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(carMapper, never()).toReadDto(any());
    }
}

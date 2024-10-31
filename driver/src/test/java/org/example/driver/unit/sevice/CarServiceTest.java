package org.example.driver.unit.sevice;

import org.example.driver.constants.AppConstants;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.entity.Car;
import org.example.driver.exception.car.DuplicatedCarNumberException;
import org.example.driver.mapper.CarMapper;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
import org.example.driver.service.CarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    private static final Long DEFAULT_ID = 1L;

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

    private Car defaultCar;
    private CarCreateEditDto createCar;
    private CarReadDto readCar;

    @BeforeEach
    void init() {
        defaultCar = new Car();
        createCar = new CarCreateEditDto("red", "BMW", "AB123CD", 2023);
        readCar = new CarReadDto(DEFAULT_ID, "red", "BMW", "AB123CD", 2023, List.of());
    }

    @Test
    void create_whenCarNumberIsNotDuplicated_thenReturnCarReadDto() {
        when(carRepository.findByNumberAndIsDeletedFalse(createCar.number()))
                .thenReturn(Optional.empty());
        when(carRepository.save(defaultCar)).then(returnsFirstArg());
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
                AppConstants.CAR_DUPLICATED_NUMBER,
                new Object[]{createCar.number()},
                LocaleContextHolder.getLocale()))
                .thenReturn(AppConstants.CAR_DUPLICATED_NUMBER);

        DuplicatedCarNumberException exception = assertThrows(DuplicatedCarNumberException.class, () -> carService.create(createCar));
        assertThat(exception.getMessage()).isEqualTo(AppConstants.CAR_DUPLICATED_NUMBER);
    }


}

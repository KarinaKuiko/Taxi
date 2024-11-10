package org.example.driver.unit.sevice;

import org.example.driver.constants.ExceptionConstants;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.UserRateDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;
import org.example.driver.exception.car.CarNotFoundException;
import org.example.driver.exception.driver.DriverNotFoundException;
import org.example.driver.exception.driver.DuplicatedDriverEmailException;
import org.example.driver.mapper.DriverMapper;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
import org.example.driver.service.DriverService;
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
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.driver.util.DataUtil.DEFAULT_ID;
import static org.example.driver.util.DataUtil.LIMIT_VALUE;
import static org.example.driver.util.DataUtil.PAGE_VALUE;
import static org.example.driver.util.DataUtil.getCar;
import static org.example.driver.util.DataUtil.getDriver;
import static org.example.driver.util.DataUtil.getDriverCreateEditDto;
import static org.example.driver.util.DataUtil.getDriverReadDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @InjectMocks
    private DriverService driverService;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private CarRepository carRepository;

    @Mock
    private MessageSource messageSource;

    private Driver defaultDriver = getDriver().build();
    private DriverCreateEditDto createDriver = getDriverCreateEditDto();
    private DriverReadDto readDriver = getDriverReadDto();
    private Car car = getCar().build();

    @Test
    void create_whenEmailIsNotDuplicatedAndCarIsFound_thenReturnDriverReadDto() {
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.empty());
        when(driverMapper.toDriver(createDriver)).thenReturn(defaultDriver);
        when(carRepository.findByIdAndIsDeletedFalse(createDriver.carId())).thenReturn(Optional.of(car));
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        assertThat(driverService.create(createDriver)).isNotNull();
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper).toDriver(createDriver);
        verify(carRepository).findByIdAndIsDeletedFalse(createDriver.carId());
        verify(driverRepository).save(defaultDriver);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void create_whenCarIsNotFound_thenThrowCarNotFoundException() {
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.empty());
        when(driverMapper.toDriver(createDriver)).thenReturn(defaultDriver);
        when(carRepository.findByIdAndIsDeletedFalse(createDriver.carId())).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{createDriver.carId()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.CAR_NOT_FOUND);

        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> driverService.create(createDriver));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.CAR_NOT_FOUND);
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper).toDriver(createDriver);
        verify(carRepository).findByIdAndIsDeletedFalse(createDriver.carId());
        verify(messageSource).getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{createDriver.carId()},
                LocaleContextHolder.getLocale());
        verify(driverRepository, never()).save(any());
        verify(driverMapper, never()).toReadDto(any());
    }

    @Test
    void create_whenEmailIsDuplicated_thenThrowDuplicatedDriverEmailException() {
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.of(defaultDriver));
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                new Object[]{createDriver.email()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_DUPLICATED_EMAIL);

        DuplicatedDriverEmailException exception = assertThrows(DuplicatedDriverEmailException.class,
                () -> driverService.create(createDriver));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.DRIVER_DUPLICATED_EMAIL);
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper, never()).toDriver(any());
        verify(carRepository, never()).findByIdAndIsDeletedFalse(any());
        verify(messageSource).getMessage(
                ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                new Object[]{createDriver.email()},
                LocaleContextHolder.getLocale());
        verify(driverRepository, never()).save(any());
        verify(driverMapper, never()).toReadDto(any());
    }

    @Test
    void update_whenEmailIsDuplicatedWithSameIds_thenReturnDriverReadDto() {
        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.of(defaultDriver));
        when(carRepository.findByIdAndIsDeletedFalse(createDriver.carId())).thenReturn(Optional.of(car));
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        assertThat(driverService.update(DEFAULT_ID, createDriver)).isNotNull();
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper).map(defaultDriver, createDriver);
        verify(carRepository).findByIdAndIsDeletedFalse(createDriver.carId());
        verify(driverRepository).save(defaultDriver);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void update_whenEmailIsDuplicatedWithDifferentIds_thenThrowDuplicatedDriverEmailException() {
        when(driverRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.of(defaultDriver));
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                new Object[]{createDriver.email()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_DUPLICATED_EMAIL);

        DuplicatedDriverEmailException exception = assertThrows(DuplicatedDriverEmailException.class,
                () -> driverService.update(2L, createDriver));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.DRIVER_DUPLICATED_EMAIL);
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper, never()).map(any(), any());
        verify(carRepository, never()).findByIdAndIsDeletedFalse(any());
        verify(messageSource).getMessage(
                ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                new Object[]{createDriver.email()},
                LocaleContextHolder.getLocale());
        verify(driverRepository, never()).save(any());
        verify(driverMapper, never()).toReadDto(any());
    }

    @Test
    void update_whenCarIsNotFound_thenThrowCarNotFoundException() {
        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.empty());
        when(carRepository.findByIdAndIsDeletedFalse(createDriver.carId())).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{createDriver.carId()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.CAR_NOT_FOUND);

        CarNotFoundException exception = assertThrows(CarNotFoundException.class,
                () -> driverService.update(DEFAULT_ID, createDriver));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.CAR_NOT_FOUND);
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(carRepository).findByIdAndIsDeletedFalse(createDriver.carId());
        verify(driverMapper).map(defaultDriver, createDriver);
        verify(driverRepository, never()).save(any());
        verify(driverMapper, never()).toReadDto(any());
        verify(messageSource).getMessage(
                ExceptionConstants.CAR_NOT_FOUND,
                new Object[]{createDriver.carId()},
                LocaleContextHolder.getLocale());
    }

    @Test
    void update_whenDriverIsNotFound_thenThrowDriverNotFoundException() {
        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_NOT_FOUND);

        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverService.update(DEFAULT_ID, createDriver));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository, never()).findByEmailAndIsDeletedFalse(any());
        verify(carRepository, never()).findByIdAndIsDeletedFalse(any());
        verify(driverMapper, never()).map(any(), any());
        verify(driverRepository, never()).save(any());
        verify(driverMapper, never()).toReadDto(any());
        verify(messageSource).getMessage(
                ExceptionConstants.DRIVER_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
    }

    @Test
    void safeDelete_whenDriverIsFound_thenMarkAsDeleted() {
        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);

        driverService.safeDelete(DEFAULT_ID);

        assertThat(defaultDriver.getCar()).isNull();
        assertThat(defaultDriver.isDeleted()).isTrue();
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository).save(defaultDriver);
    }

    @Test
    void safeDelete_whenDriverIsNotFound_thenThrowDriverNotFoundException() {
        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_NOT_FOUND);

        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverService.update(DEFAULT_ID, createDriver));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(messageSource).getMessage(
                ExceptionConstants.DRIVER_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(driverRepository, never()).save(any());
    }

    @Test
    void findAll_thenReturnPageDriverReadDto() {
        Pageable request = PageRequest.of(PAGE_VALUE, LIMIT_VALUE);

        when(driverRepository.findByIsDeletedFalse(request)).thenReturn(
                new PageImpl<>(List.of(defaultDriver), request, 1));
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        Page<DriverReadDto> result = driverService.findAll(PAGE_VALUE, LIMIT_VALUE);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(driverRepository).findByIsDeletedFalse(request);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void findAllWithDeleted_thenReturnPageDriverReadDto() {
        Pageable request = PageRequest.of(PAGE_VALUE, LIMIT_VALUE);

        when(driverRepository.findAll(request)).thenReturn(new PageImpl<>(List.of(defaultDriver), request, 1));
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        Page<DriverReadDto> result = driverService.findAllWithDeleted(PAGE_VALUE, LIMIT_VALUE);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(driverRepository).findAll(request);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void findById_whenDriverIsFound_thenReturnDriverReadDto() {
        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        assertThat(driverService.findById(DEFAULT_ID)).isNotNull();
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void findById_whenDriverIsNotFound_thenThrowDriverNotFoundException() {
        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_NOT_FOUND);

        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverService.findById(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.DRIVER_NOT_FOUND);
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(messageSource).getMessage(
                ExceptionConstants.DRIVER_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(driverMapper, never()).toReadDto(any());
    }

    @Test
    void updateRating_thenUpdateRating() {
        UserRateDto rateDto = new UserRateDto(DEFAULT_ID, 4.0);

        when(driverRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);

        driverService.updateRating(rateDto);

        assertThat(defaultDriver.getRating()).isEqualTo(4.0);
        verify(driverRepository).findById(DEFAULT_ID);
        verify(driverRepository).save(defaultDriver);
    }
}

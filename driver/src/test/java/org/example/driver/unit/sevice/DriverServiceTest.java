package org.example.driver.unit.sevice;

import org.example.driver.constants.ExceptionConstants;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.UserRateDto;
import org.example.driver.entity.Car;
import org.example.driver.entity.Driver;
import org.example.driver.exception.driver.DriverNotFoundException;
import org.example.driver.exception.driver.DuplicatedDriverEmailException;
import org.example.driver.mapper.CarMapper;
import org.example.driver.mapper.DriverMapper;
import org.example.driver.repository.CarRepository;
import org.example.driver.repository.DriverRepository;
import org.example.driver.service.CarService;
import org.example.driver.service.DriverService;
import org.example.driver.service.ImageStorageService;
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
import static org.example.driver.util.DataUtil.getCarBuilder;
import static org.example.driver.util.DataUtil.getCarReadDtoBuilder;
import static org.example.driver.util.DataUtil.getDriverBuilder;
import static org.example.driver.util.DataUtil.getDriverCreateEditDtoBuilder;
import static org.example.driver.util.DataUtil.getDriverReadDtoBuilder;
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
    private CarService carService;

    @Mock
    private ImageStorageService imageStorageService;

    @Mock
    private CarMapper carMapper;

    @Mock
    private MessageSource messageSource;

    @Test
    void create_whenEmailIsNotDuplicatedAndCarIsFound_thenReturnDriverReadDto() {
        Driver defaultDriver = getDriverBuilder().build();
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();
        Car car = getCarBuilder().build();

        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.empty());
        when(driverMapper.toDriver(createDriver)).thenReturn(defaultDriver);
        when(carRepository.findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number())).thenReturn(Optional.of(car));
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        assertThat(driverService.create(createDriver, null)).isNotNull();
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper).toDriver(createDriver);
        verify(carRepository).findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number());
        verify(driverRepository).save(defaultDriver);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void create_whenCarIsNotFound_thenReturnDriverReadDto() {
        Driver defaultDriver = getDriverBuilder().build();
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();
        Car car = getCarBuilder().build();
        CarReadDto carReadDto = getCarReadDtoBuilder().build();

        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.empty());
        when(driverMapper.toDriver(createDriver)).thenReturn(defaultDriver);
        when(carRepository.findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number())).thenReturn(Optional.empty());
        when(carService.create(createDriver.carCreateEditDto())).thenReturn(carReadDto);
        when(carMapper.toCar(carReadDto)).thenReturn(car);
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        assertThat(driverService.create(createDriver, null)).isNotNull();
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper).toDriver(createDriver);
        verify(carRepository).findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number());
        verify(carService).create(createDriver.carCreateEditDto());
        verify(carMapper).toCar(carReadDto);
        verify(driverRepository).save(defaultDriver);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void create_whenEmailIsDuplicated_thenThrowDuplicatedDriverEmailException() {
        Driver defaultDriver = getDriverBuilder().build();
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.of(defaultDriver));
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                new Object[]{createDriver.email()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_DUPLICATED_EMAIL);

        DuplicatedDriverEmailException exception = assertThrows(DuplicatedDriverEmailException.class,
                () -> driverService.create(createDriver, null));

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
        Driver defaultDriver = getDriverBuilder().build();
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();
        Car car = getCarBuilder().build();

        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.of(defaultDriver));
        when(carRepository.findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number())).thenReturn(Optional.of(car));
        when(imageStorageService.updateImage(null, null)).thenReturn(null);
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        assertThat(driverService.update(DEFAULT_ID, createDriver, null)).isNotNull();
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(driverMapper).map(defaultDriver, createDriver);
        verify(carRepository).findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number());
        verify(driverRepository).save(defaultDriver);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void update_whenEmailIsDuplicatedWithDifferentIds_thenThrowDuplicatedDriverEmailException() {
        Driver defaultDriver = getDriverBuilder().build();
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

        when(driverRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.of(defaultDriver));
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_DUPLICATED_EMAIL,
                new Object[]{createDriver.email()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_DUPLICATED_EMAIL);

        DuplicatedDriverEmailException exception = assertThrows(DuplicatedDriverEmailException.class,
                () -> driverService.update(2L, createDriver, null));

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
    void update_whenCarIsNotFound_thenReturnDriverReadDto() {
        Driver defaultDriver = getDriverBuilder().build();
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();
        Car car = getCarBuilder().build();
        CarReadDto carReadDto = getCarReadDtoBuilder().build();

        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.findByEmailAndIsDeletedFalse(createDriver.email()))
                .thenReturn(Optional.empty());
        when(carRepository.findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number())).thenReturn(Optional.empty());
        when(carService.create(createDriver.carCreateEditDto())).thenReturn(carReadDto);
        when(carMapper.toCar(carReadDto)).thenReturn(car);
        when(imageStorageService.updateImage(null, null)).thenReturn(null);
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);
        when(driverMapper.toReadDto(defaultDriver)).thenReturn(readDriver);

        assertThat(driverService.update(DEFAULT_ID, createDriver, null)).isNotNull();
        verify(driverRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(driverRepository).findByEmailAndIsDeletedFalse(createDriver.email());
        verify(carRepository).findByNumberAndIsDeletedFalse(createDriver.carCreateEditDto().number());
        verify(driverMapper).map(defaultDriver, createDriver);
        verify(carService).create(createDriver.carCreateEditDto());
        verify(carMapper).toCar(carReadDto);
        verify(driverRepository).save(defaultDriver);
        verify(driverMapper).toReadDto(defaultDriver);
    }

    @Test
    void update_whenDriverIsNotFound_thenThrowDriverNotFoundException() {
        DriverCreateEditDto createDriver = getDriverCreateEditDtoBuilder().build();

        when(driverRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.DRIVER_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.DRIVER_NOT_FOUND);

        DriverNotFoundException exception = assertThrows(DriverNotFoundException.class,
                () -> driverService.update(DEFAULT_ID, createDriver, null));

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
        Driver defaultDriver = getDriverBuilder().build();

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
                () -> driverService.safeDelete(DEFAULT_ID));

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
        Driver defaultDriver = getDriverBuilder().build();
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();
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
        Driver defaultDriver = getDriverBuilder().build();
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();
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
        Driver defaultDriver = getDriverBuilder().build();
        DriverReadDto readDriver = getDriverReadDtoBuilder().build();
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
        Driver defaultDriver = getDriverBuilder().build();
        UserRateDto rateDto = new UserRateDto(DEFAULT_ID, 4.0);

        when(driverRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultDriver));
        when(driverRepository.save(defaultDriver)).thenReturn(defaultDriver);

        driverService.updateRating(rateDto);

        assertThat(defaultDriver.getRating()).isEqualTo(4.0);
        verify(driverRepository).findById(DEFAULT_ID);
        verify(driverRepository).save(defaultDriver);
    }
}

package org.example.ride.unit.service;

import org.example.ride.constants.ExceptionConstants;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.DriverReadDto;
import org.example.ride.dto.read.PassengerReadDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.DriverRideStatus;
import org.example.ride.entity.enumeration.PassengerRideStatus;
import org.example.ride.exception.param.InvalidCountParametersException;
import org.example.ride.exception.ride.RideNotFoundException;
import org.example.ride.kafka.KafkaProducer;
import org.example.ride.mapper.RideMapper;
import org.example.ride.repository.RideRepository;
import org.example.ride.service.DriverClientService;
import org.example.ride.service.PassengerClientService;
import org.example.ride.service.RideService;
import org.example.ride.utils.PriceGenerator;
import org.example.ride.validation.RideStatusValidation;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {

    private static final Long DEFAULT_ID = 1L;

    @InjectMocks
    private RideService rideService;

    @Mock
    private RideRepository rideRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private RideMapper rideMapper;

    @Mock
    private PriceGenerator priceGenerator;

    @Mock
    private RideStatusValidation rideStatusValidation;

    @Mock
    private PassengerClientService passengerClient;

    @Mock
    private DriverClientService driverClient;

    @Mock
    private KafkaProducer kafkaProducer;

    private Ride defaultRide;
    private RideCreateEditDto createRide;
    private RideReadDto readRide;

    @BeforeEach
    void init() {
        defaultRide = new Ride(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, "from",
                "to", DriverRideStatus.ACCEPTED, PassengerRideStatus.WAITING, new BigDecimal("123.45"));
        createRide = new RideCreateEditDto(DEFAULT_ID, DEFAULT_ID, "from", "to");
        readRide = new RideReadDto(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, "from",
                "to", DriverRideStatus.ACCEPTED, PassengerRideStatus.WAITING, new BigDecimal("123.45"));
    }

    @Test
    void findRides_whenDriverAndPassengerIdsEnter_thenThrowInvalidCountParametersException() {
        when(messageSource.getMessage(
                ExceptionConstants.INVALID_COUNT_PARAMETERS_MESSAGE,
                new Object[]{},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.INVALID_COUNT_PARAMETERS_MESSAGE);

        InvalidCountParametersException exception = assertThrows(InvalidCountParametersException.class,
                () -> rideService.findRides(DEFAULT_ID, DEFAULT_ID, 1, 10));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.INVALID_COUNT_PARAMETERS_MESSAGE);
        verify(messageSource).getMessage(
                ExceptionConstants.INVALID_COUNT_PARAMETERS_MESSAGE,
                new Object[]{},
                LocaleContextHolder.getLocale());
    }

    @Test
    void findRides_whenDriverIdEnter_thenReturnPageRideReadDto() {
        int page = 0, limit = 10;
        Pageable request = PageRequest.of(page, limit);

        when(rideRepository.findByDriverId(DEFAULT_ID, request))
                .thenReturn(new PageImpl<>(List.of(defaultRide), request, 1));
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        Page<RideReadDto> result = rideService.findRides(DEFAULT_ID, null, page, limit);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(rideRepository).findByDriverId(DEFAULT_ID, request);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void findRides_whenPassengerIdEnter_thenReturnPageRideReadDto() {
        int page = 0, limit = 10;
        Pageable request = PageRequest.of(page, limit);

        when(rideRepository.findByPassengerId(DEFAULT_ID, request))
                .thenReturn(new PageImpl<>(List.of(defaultRide), request, 1));
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        Page<RideReadDto> result = rideService.findRides(null, DEFAULT_ID, page, limit);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(rideRepository).findByPassengerId(DEFAULT_ID, request);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void findAll_thenReturnPageRideReadDto() {
        int page = 0, limit = 10;
        Pageable request = PageRequest.of(page, limit);

        when(rideRepository.findAll(request))
                .thenReturn(new PageImpl<>(List.of(defaultRide), request, 1));
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        Page<RideReadDto> result = rideService.findAll(page, limit);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(rideRepository).findAll(request);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void findById_whenRideIsFound_thenReturnRideReadDto() {
        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRide));
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        assertThat(rideService.findById(DEFAULT_ID)).isNotNull();
        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void findById_whenRideIsNotFound_thenThrowRideNotFoundException() {
        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);

        RideNotFoundException exception = assertThrows(RideNotFoundException.class,
                () -> rideService.findById(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);
        verify(messageSource).getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideMapper, never()).toReadDto(any());
    }

    @Test
    void findByPassengerId_whenIdEnter_thenReturnPageRideReadDto() {
        int page = 0, limit = 10;
        Pageable request = PageRequest.of(page, limit);

        when(rideRepository.findByPassengerId(DEFAULT_ID, request))
                .thenReturn(new PageImpl<>(List.of(defaultRide), request, 1));
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        Page<RideReadDto> result = rideService.findByPassengerId(DEFAULT_ID, page, limit);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(rideRepository).findByPassengerId(DEFAULT_ID, request);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void findByDriverId_whenIdEnter_thenReturnPageRideReadDto() {
        int page = 0, limit = 10;
        Pageable request = PageRequest.of(page, limit);

        when(rideRepository.findByDriverId(DEFAULT_ID, request))
                .thenReturn(new PageImpl<>(List.of(defaultRide), request, 1));
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        Page<RideReadDto> result = rideService.findByDriverId(DEFAULT_ID, page, limit);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(rideRepository).findByDriverId(DEFAULT_ID, request);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void create_whenRideCreateEditDtoEnter_thenReturnRideReadDto() {
        DriverReadDto readDriver = new DriverReadDto(DEFAULT_ID, "name",
                "name@gmail.com", "+375331234567", "MALE", DEFAULT_ID, 5.0);
        PassengerReadDto readPassenger = new PassengerReadDto(DEFAULT_ID, "name",
                "name@gmail.com", "+375331234567", 5.0);

        when(driverClient.checkExistingDriver(DEFAULT_ID)).thenReturn(readDriver);
        when(passengerClient.checkExistingPassenger(DEFAULT_ID)).thenReturn(readPassenger);
        when(rideMapper.toRide(createRide)).thenReturn(defaultRide);
        when(rideRepository.save(defaultRide)).thenReturn(defaultRide);
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        assertThat(rideService.create(createRide)).isNotNull();
        assertThat(defaultRide.getDriverRideStatus()).isEqualTo(DriverRideStatus.CREATED);
        assertThat(defaultRide.getPassengerRideStatus()).isEqualTo(PassengerRideStatus.WAITING);
        verify(driverClient).checkExistingDriver(DEFAULT_ID);
        verify(passengerClient).checkExistingPassenger(DEFAULT_ID);
        verify(rideMapper).toRide(createRide);
        verify(rideRepository).save(defaultRide);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void update_whenRideIsFound_thenReturnRideReadDto() {
        DriverReadDto readDriver = new DriverReadDto(DEFAULT_ID, "name",
                "name@gmail.com", "+375331234567", "MALE", DEFAULT_ID, 5.0);
        PassengerReadDto readPassenger = new PassengerReadDto(DEFAULT_ID, "name",
                "name@gmail.com", "+375331234567", 5.0);

        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRide));
        when(driverClient.checkExistingDriver(DEFAULT_ID)).thenReturn(readDriver);
        when(passengerClient.checkExistingPassenger(DEFAULT_ID)).thenReturn(readPassenger);
        when(rideRepository.save(defaultRide)).thenReturn(defaultRide);
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        assertThat(rideService.update(DEFAULT_ID, createRide)).isNotNull();
        verify(driverClient).checkExistingDriver(DEFAULT_ID);
        verify(passengerClient).checkExistingPassenger(DEFAULT_ID);
        verify(rideMapper).map(defaultRide, createRide);
        verify(rideRepository).save(defaultRide);
        verify(rideMapper).toReadDto(defaultRide);
    }

    @Test
    void update_whenRideIsNotFound_thenThrowRideNotFoundException() {
        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);

        RideNotFoundException exception = assertThrows(RideNotFoundException.class,
                () -> rideService.findById(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);
        verify(messageSource).getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(rideRepository).findById(DEFAULT_ID);
        verify(driverClient, never()).checkExistingDriver(any());
        verify(passengerClient, never()).checkExistingPassenger(any());
        verify(rideMapper, never()).map(any(), any());
        verify(rideRepository, never()).save(any());
        verify(rideMapper, never()).toReadDto(any());
    }

    @Test
    void updateDriverStatus_whenProposedStatusIsOnWayToDestination_thenChangeStatusAndReturnRideReadDto() {
        defaultRide.setDriverRideStatus(DriverRideStatus.WAITING);
        DriverRideStatus proposed = DriverRideStatus.ON_WAY_TO_DESTINATION;

        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRide));
        when(rideRepository.save(defaultRide)).thenReturn(defaultRide);
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        assertThat(rideService.updateDriverStatus(DEFAULT_ID, new DriverRideStatusDto(proposed))).isNotNull();
        verify(rideRepository, times(2)).findById(DEFAULT_ID);
        verify(rideStatusValidation).validateUpdatingDriverStatus(defaultRide, proposed);
        verify(rideMapper).mapDriverStatus(defaultRide, proposed);
        verify(rideStatusValidation).validateUpdatingPassengerStatus(defaultRide);
        verify(rideMapper).mapPassengerStatus(defaultRide, PassengerRideStatus.IN_CAR);
        verify(rideStatusValidation).validateUpdatingPassengerStatus(defaultRide);
        verify(rideMapper, times(2)).toReadDto(defaultRide);
        verify(rideRepository, times(2)).save(defaultRide);
        verify(kafkaProducer).notifyDriver(readRide);
        verify(kafkaProducer).notifyPassenger(readRide);
    }

    @Test
    void updateDriverStatus_whenRideIsFound_thenChangeStatusAndReturnRideReadDto() {
        DriverRideStatus proposed = DriverRideStatus.ON_WAY_FOR_PASSENGER;

        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRide));
        when(rideRepository.save(defaultRide)).thenReturn(defaultRide);
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        assertThat(rideService.updateDriverStatus(DEFAULT_ID, new DriverRideStatusDto(proposed))).isNotNull();
        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideStatusValidation).validateUpdatingDriverStatus(defaultRide, proposed);
        verify(rideMapper).mapDriverStatus(defaultRide, proposed);
        verify(rideMapper).toReadDto(defaultRide);
        verify(rideRepository).save(defaultRide);
        verify(kafkaProducer).notifyPassenger(readRide);
    }

    @Test
    void updateDriverStatus_whenRideIsNotFound_thenThrowRideNotFoundException() {
        DriverRideStatus proposed = DriverRideStatus.ON_WAY_FOR_PASSENGER;

        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);

        RideNotFoundException exception = assertThrows(RideNotFoundException.class,
                () -> rideService.updateDriverStatus(DEFAULT_ID, new DriverRideStatusDto(proposed)));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);
        verify(messageSource).getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideMapper, never()).toReadDto(any());
        verify(rideStatusValidation, never()).validateUpdatingDriverStatus(any(), any());
        verify(rideMapper, never()).mapDriverStatus(any(), any());
        verify(rideRepository, never()).save(any());
        verify(kafkaProducer, never()).notifyPassenger(any());
    }

    @Test
    void updatePassengerStatus_whenRideIsFound_thenChangeStatusAndReturnRideReadDto() {
        PassengerRideStatus proposed = PassengerRideStatus.GETTING_OUT;

        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRide));
        when(rideRepository.save(defaultRide)).thenReturn(defaultRide);
        when(rideMapper.toReadDto(defaultRide)).thenReturn(readRide);

        assertThat(rideService.updatePassengerStatus(DEFAULT_ID, new PassengerRideStatusDto(proposed))).isNotNull();
        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideStatusValidation).validateUpdatingPassengerStatus(defaultRide);
        verify(rideMapper).mapPassengerStatus(defaultRide, proposed);
        verify(rideMapper).toReadDto(defaultRide);
        verify(rideRepository).save(defaultRide);
        verify(kafkaProducer).notifyDriver(readRide);
    }

    @Test
    void updatePassengerStatus_whenRideIsNotFound_thenThrowRideNotFoundException() {
        PassengerRideStatus proposed = PassengerRideStatus.GETTING_OUT;

        when(rideRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);

        RideNotFoundException exception = assertThrows(RideNotFoundException.class,
                () -> rideService.updatePassengerStatus(DEFAULT_ID, new PassengerRideStatusDto(proposed)));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE);
        verify(messageSource).getMessage(
                ExceptionConstants.RIDE_NOT_FOUND_EXCEPTION_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(rideRepository).findById(DEFAULT_ID);
        verify(rideMapper, never()).toReadDto(any());
        verify(rideStatusValidation, never()).validateUpdatingPassengerStatus(any());
        verify(rideMapper, never()).mapPassengerStatus(any(), any());
        verify(rideRepository, never()).save(any());
        verify(kafkaProducer, never()).notifyDriver(any());
    }
}

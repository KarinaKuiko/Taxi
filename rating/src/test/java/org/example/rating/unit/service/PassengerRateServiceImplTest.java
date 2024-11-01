package org.example.rating.unit.service;

import org.example.rating.constants.ExceptionConstants;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.RideReadDto;
import org.example.rating.entity.DriverRate;
import org.example.rating.entity.PassengerRate;
import org.example.rating.entity.enumeration.UserType;
import org.example.rating.exception.rate.RateNotFoundException;
import org.example.rating.kafka.KafkaProducer;
import org.example.rating.mapper.RateMapper;
import org.example.rating.repository.PassengerRateRepository;
import org.example.rating.service.RateCounterService;
import org.example.rating.service.RideClientService;
import org.example.rating.service.impl.PassengerRateService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PassengerRateServiceImplTest {
    private static final Long DEFAULT_ID = 1L;

    @InjectMocks
    private PassengerRateService passengerRateService;

    @Mock
    private PassengerRateRepository passengerRateRepository;

    @Mock
    private RateMapper rateMapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private RideClientService rideClient;

    @Mock
    private RateCounterService rateCounterService;

    @Mock
    private KafkaProducer kafkaProducer;

    private PassengerRate defaultRate;
    private RateCreateEditDto createRate;
    private RateReadDto readRate;
    private RideReadDto readRide;

    @BeforeEach
    void init() {
        readRide = new RideReadDto(DEFAULT_ID, DEFAULT_ID, DEFAULT_ID, "from", "to", "ACCEPTED", "WAITING", new BigDecimal("123.45"));
        defaultRate = new PassengerRate(DEFAULT_ID, DEFAULT_ID, "Great ride!", 5, DEFAULT_ID, UserType.DRIVER);
        createRate = new RateCreateEditDto(DEFAULT_ID, "Great ride!", 5, DEFAULT_ID, UserType.DRIVER);
        readRate = new RateReadDto(DEFAULT_ID, DEFAULT_ID, "Great ride!", 5, DEFAULT_ID, UserType.DRIVER);
    }

    @Test
    void findAll_thenReturnPageRateReadDto() {
        int page = 0, limit = 10;
        Pageable request = PageRequest.of(page, limit);

        when(passengerRateRepository.findAll(request)).thenReturn(new PageImpl<>(List.of(defaultRate), request, 1));
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        Page<RateReadDto> result = passengerRateService.findAll(page, limit);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(passengerRateRepository).findAll(request);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void findById_whenRateIsFound_thenReturnRateReadDto() {
        when(passengerRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRate));
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(passengerRateService.findById(DEFAULT_ID)).isNotNull();
        verify(passengerRateRepository).findById(DEFAULT_ID);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void findById_whenRateIsNotFound_thenThrowRateNotFoundException() {
        when(passengerRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RATE_NOT_FOUND);

        RateNotFoundException exception = assertThrows(RateNotFoundException.class, () -> passengerRateService.findById(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RATE_NOT_FOUND);
        verify(passengerRateRepository).findById(DEFAULT_ID);
        verify(messageSource).getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(rateMapper, never()).toReadDto(any(DriverRate.class));
    }

    @Test
    void create_whenRideIsExists_thenReturnRateReadDto() {
        when(rateMapper.toPassengerRate(createRate)).thenReturn(defaultRate);
        when(rideClient.checkExistingRide(DEFAULT_ID)).thenReturn(readRide);
        when(passengerRateRepository.save(defaultRate)).thenReturn(defaultRate);
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(passengerRateService.create(createRate)).isNotNull();
        verify(rateMapper).toPassengerRate(createRate);
        verify(rideClient).checkExistingRide(DEFAULT_ID);
        verify(passengerRateRepository).save(defaultRate);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void update_whenRateIsFound_thenReturnRateReadDto() {
        when(passengerRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRate));
        when(rideClient.checkExistingRide(DEFAULT_ID)).thenReturn(readRide);
        when(passengerRateRepository.save(defaultRate)).thenReturn(defaultRate);
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(passengerRateService.update(DEFAULT_ID, createRate)).isNotNull();
        verify(passengerRateRepository).findById(DEFAULT_ID);
        verify(rideClient).checkExistingRide(DEFAULT_ID);
        verify(rateMapper).map(defaultRate, createRate);
        verify(passengerRateRepository).save(defaultRate);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void update_whenRateIsNotFound_thenThrowRateNotFoundException() {
        when(passengerRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RATE_NOT_FOUND);

        RateNotFoundException exception = assertThrows(RateNotFoundException.class, () -> passengerRateService.update(DEFAULT_ID, createRate));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RATE_NOT_FOUND);
        verify(passengerRateRepository).findById(DEFAULT_ID);
        verify(rideClient, never()).checkExistingRide(any());
        verify(rateMapper, never()).map(any(), any());
        verify(passengerRateRepository, never()).save(any());
        verify(rateMapper, never()).toReadDto(any(DriverRate.class));
        verify(messageSource).getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
    }

    @Test
    void findByUserId_thenReturnListRateReadDto() {
        when(passengerRateRepository.findByUserId(DEFAULT_ID)).thenReturn(List.of(defaultRate));
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(passengerRateService.findByUserId(DEFAULT_ID)).isNotNull();
        verify(passengerRateRepository).findByUserId(DEFAULT_ID);
        verify(rateMapper).toReadDto(defaultRate);
    }
}

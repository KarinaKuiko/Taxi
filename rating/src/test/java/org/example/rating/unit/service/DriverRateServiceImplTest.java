package org.example.rating.unit.service;

import com.example.exceptionhandlerstarter.exception.rate.RateNotFoundException;
import org.example.rating.constants.ExceptionConstants;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.RideReadDto;
import org.example.rating.entity.DriverRate;
import org.example.rating.kafka.KafkaProducer;
import org.example.rating.mapper.RateMapper;
import org.example.rating.repository.DriverRateRepository;
import org.example.rating.service.RateCounterService;
import org.example.rating.service.RideClientService;
import org.example.rating.service.impl.DriverRateService;
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
import static org.example.rating.util.DataUtil.DEFAULT_ID;
import static org.example.rating.util.DataUtil.LIMIT_VALUE;
import static org.example.rating.util.DataUtil.PAGE_VALUE;
import static org.example.rating.util.DataUtil.getDriverRateBuilder;
import static org.example.rating.util.DataUtil.getDriverRateCreateEditDtoBuilder;
import static org.example.rating.util.DataUtil.getDriverRateReadDtoBuilder;
import static org.example.rating.util.DataUtil.getRideReadDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverRateServiceImplTest {

    @InjectMocks
    private DriverRateService driverRateService;

    @Mock
    private DriverRateRepository driverRateRepository;

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

    @Test
    void findAll_thenReturnPageRateReadDto() {
        DriverRate defaultRate = getDriverRateBuilder().build();
        RateReadDto readRate = getDriverRateReadDtoBuilder().build();
        Pageable request = PageRequest.of(PAGE_VALUE, LIMIT_VALUE);

        when(driverRateRepository.findAll(request)).thenReturn(new PageImpl<>(List.of(defaultRate), request, 1));
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        Page<RateReadDto> result = driverRateService.findAll(PAGE_VALUE, LIMIT_VALUE);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(driverRateRepository).findAll(request);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void findById_whenRateIsFound_thenReturnRateReadDto() {
        DriverRate defaultRate = getDriverRateBuilder().build();
        RateReadDto readRate = getDriverRateReadDtoBuilder().build();

        when(driverRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRate));
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(driverRateService.findById(DEFAULT_ID)).isNotNull();
        verify(driverRateRepository).findById(DEFAULT_ID);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void findById_whenRateIsNotFound_thenThrowRateNotFoundException() {
        when(driverRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RATE_NOT_FOUND);

        RateNotFoundException exception = assertThrows(RateNotFoundException.class,
                () -> driverRateService.findById(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RATE_NOT_FOUND);
        verify(driverRateRepository).findById(DEFAULT_ID);
        verify(messageSource).getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(rateMapper, never()).toReadDto(any(DriverRate.class));
    }

    @Test
    void create_whenRideIsExists_thenReturnRateReadDto() {
        DriverRate defaultRate = getDriverRateBuilder().build();
        RateCreateEditDto createRate = getDriverRateCreateEditDtoBuilder().build();
        RateReadDto readRate = getDriverRateReadDtoBuilder().build();
        RideReadDto readRide = getRideReadDtoBuilder().build();

        when(rateMapper.toDriverRate(createRate)).thenReturn(defaultRate);
        when(rideClient.getRide(DEFAULT_ID)).thenReturn(readRide);
        when(driverRateRepository.save(defaultRate)).thenReturn(defaultRate);
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(driverRateService.create(createRate)).isNotNull();
        verify(rateMapper).toDriverRate(createRate);
        verify(rideClient).getRide(DEFAULT_ID);
        verify(driverRateRepository).save(defaultRate);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void update_whenRateIsFound_thenReturnRateReadDto() {
        DriverRate defaultRate = getDriverRateBuilder().build();
        RateCreateEditDto createRate = getDriverRateCreateEditDtoBuilder().build();
        RateReadDto readRate = getDriverRateReadDtoBuilder().build();
        RideReadDto readRide = getRideReadDtoBuilder().build();

        when(driverRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultRate));
        when(rideClient.getRide(DEFAULT_ID)).thenReturn(readRide);
        when(driverRateRepository.save(defaultRate)).thenReturn(defaultRate);
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(driverRateService.update(DEFAULT_ID, createRate)).isNotNull();
        verify(driverRateRepository).findById(DEFAULT_ID);
        verify(rideClient).getRide(DEFAULT_ID);
        verify(rateMapper).map(defaultRate, createRate);
        verify(driverRateRepository).save(defaultRate);
        verify(rateMapper).toReadDto(defaultRate);
    }

    @Test
    void update_whenRateIsNotFound_thenThrowRateNotFoundException() {
        RateCreateEditDto createRate = getDriverRateCreateEditDtoBuilder().build();

        when(driverRateRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.RATE_NOT_FOUND);

        RateNotFoundException exception = assertThrows(RateNotFoundException.class,
                () -> driverRateService.update(DEFAULT_ID, createRate));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.RATE_NOT_FOUND);
        verify(driverRateRepository).findById(DEFAULT_ID);
        verify(rideClient, never()).getRide(any());
        verify(rateMapper, never()).map(any(DriverRate.class), any());
        verify(driverRateRepository, never()).save(any());
        verify(rateMapper, never()).toReadDto(any(DriverRate.class));
        verify(messageSource).getMessage(
                ExceptionConstants.RATE_NOT_FOUND,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
    }

    @Test
    void findByUserId_thenReturnListRateReadDto() {
        DriverRate defaultRate = getDriverRateBuilder().build();
        RateReadDto readRate = getDriverRateReadDtoBuilder().build();

        when(driverRateRepository.findByUserId(DEFAULT_ID)).thenReturn(List.of(defaultRate));
        when(rateMapper.toReadDto(defaultRate)).thenReturn(readRate);

        assertThat(driverRateService.findByUserId(DEFAULT_ID)).isNotNull();
        verify(driverRateRepository).findByUserId(DEFAULT_ID);
        verify(rateMapper).toReadDto(defaultRate);
    }
}

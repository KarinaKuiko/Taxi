package org.example.rating.service.impl;

import com.example.exceptionhandlerstarter.exception.rate.RateNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.rating.constants.ExceptionConstants;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.RideReadDto;
import org.example.rating.dto.read.UserRateDto;
import org.example.rating.entity.PassengerRate;
import org.example.rating.kafka.KafkaProducer;
import org.example.rating.mapper.RateMapper;
import org.example.rating.repository.PassengerRateRepository;
import org.example.rating.service.RateCounterService;
import org.example.rating.service.RateService;
import org.example.rating.service.RideClientService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.rating.constants.RedisConstants.PASSENGER_RATE_CACHE_VALUE;

@Service
@RequiredArgsConstructor
public class PassengerRateService implements RateService {
    public final PassengerRateRepository passengerRateRepository;
    public final RateMapper rateMapper;
    public final MessageSource messageSource;
    public final RideClientService rideClient;
    public final RateCounterService rateCounterService;
    private final KafkaProducer kafkaProducer;

    @Override
    public Page<RateReadDto> findAll(Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);
        return passengerRateRepository.findAll(request)
                .map(rateMapper::toReadDto);
    }

    @Override
    @Cacheable(value = PASSENGER_RATE_CACHE_VALUE, key = "#id")
    public RateReadDto findById(Long id) {
        return passengerRateRepository.findById(id)
                .map(rateMapper::toReadDto)
                .orElseThrow(() -> new RateNotFoundException(messageSource.getMessage(
                        ExceptionConstants.RATE_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()
                )));
    }

    @Override
    @Transactional
    @CachePut(value = PASSENGER_RATE_CACHE_VALUE, key = "#result.id()")
    public RateReadDto create(RateCreateEditDto rateDto) {
        PassengerRate rate = rateMapper.toPassengerRate(rateDto);
        RideReadDto rideReadDto = rideClient.getRide(rate.getRideId());
        rate = passengerRateRepository.save(rate);
        updateAverageRating(rideReadDto.passengerId());
        return rateMapper.toReadDto(rate);
    }

    @Override
    @Transactional
    @CachePut(value = PASSENGER_RATE_CACHE_VALUE, key = "#id")
    public RateReadDto update(Long id, RateCreateEditDto rateDto) {
        return passengerRateRepository.findById(id)
                .map(rate -> {
                    RideReadDto rideReadDto = rideClient.getRide(rateDto.rideId());
                    rateMapper.map(rate, rateDto);
                    passengerRateRepository.save(rate);
                    updateAverageRating(rideReadDto.passengerId());
                    return rate;
                })
                .map(rateMapper::toReadDto)
                .orElseThrow(() -> new RateNotFoundException(messageSource.getMessage(
                        ExceptionConstants.RATE_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    @Override
    public List<RateReadDto> findByUserId(Long id) {
        return passengerRateRepository.findByUserId(id)
                .stream()
                .map(rateMapper::toReadDto)
                .toList();
    }

    private void updateAverageRating(Long userId) {
        List<RateReadDto> rateReadDtoList = findByUserId(userId);
        double averageRating = rateCounterService.countRating(rateReadDtoList);
        UserRateDto userRatingDto = new UserRateDto(userId, averageRating);
        kafkaProducer.notifyPassenger(userRatingDto);
    }
}

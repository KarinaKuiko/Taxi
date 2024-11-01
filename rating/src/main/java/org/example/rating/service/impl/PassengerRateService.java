package org.example.rating.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.rating.constants.ExceptionConstants;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.RideReadDto;
import org.example.rating.dto.read.UserRateDto;
import org.example.rating.entity.PassengerRate;
import org.example.rating.exception.rate.RateNotFoundException;
import org.example.rating.kafka.KafkaProducer;
import org.example.rating.mapper.RateMapper;
import org.example.rating.repository.PassengerRateRepository;
import org.example.rating.service.RateCounterService;
import org.example.rating.service.RateService;
import org.example.rating.service.RideClientService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public RateReadDto create(RateCreateEditDto rateDto) {
        PassengerRate rate = rateMapper.toPassengerRate(rateDto);
        RideReadDto rideReadDto = rideClient.checkExistingRide(rate.getRideId());
        rate = passengerRateRepository.save(rate);
        updateAverageRating(rideReadDto.passengerId());
        return rateMapper.toReadDto(rate);
    }

    @Override
    @Transactional
    public RateReadDto update(Long id, RateCreateEditDto rateDto) {
        return passengerRateRepository.findById(id)
                .map(rate -> {
                    RideReadDto rideReadDto = rideClient.checkExistingRide(rateDto.rideId());
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

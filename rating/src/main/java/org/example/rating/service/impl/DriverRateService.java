package org.example.rating.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.rating.constants.AppConstants;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.dto.read.UserRateDto;
import org.example.rating.entity.DriverRate;
import org.example.rating.exception.rate.RateNotFoundException;
import org.example.rating.mapper.RateMapper;
import org.example.rating.repository.DriverRateRepository;
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
public class DriverRateService implements RateService {
    public final DriverRateRepository driverRateRepository;
    public final RateMapper rateMapper;
    public final MessageSource messageSource;
    public final RideClientService rideClient;
    private final RateCounterService rateCounterService;

    @Override
    public Page<RateReadDto> findAll(Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);
        return driverRateRepository.findAll(request)
                .map(rateMapper::toReadDto);
    }

    @Override
    public RateReadDto findById(Long id) {
        return driverRateRepository.findById(id)
                .map(rateMapper::toReadDto)
                .orElseThrow(() -> new RateNotFoundException(messageSource.getMessage(
                        AppConstants.RATE_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()
                )));
    }

    @Override
    @Transactional
    public RateReadDto create(RateCreateEditDto rateDto) {
        DriverRate rate = rateMapper.toDriverRate(rateDto); //TODO: fill field userId
        rideClient.checkExistingRide(rate.getRideId());
        updateAverageRating(rate.getUserId());
        return rateMapper.toReadDto(driverRateRepository.save(rate));
    }

    @Override
    @Transactional
    public RateReadDto update(Long id, RateCreateEditDto rateDto) {
        return driverRateRepository.findById(id)
                .map(rate -> {
                    rideClient.checkExistingRide(rateDto.rideId());
                    rateMapper.map(rate, rateDto);
                    updateAverageRating(rateDto.userId());
                    return rate;
                })
                .map(driverRateRepository::save)
                .map(rateMapper::toReadDto)
                .orElseThrow(() -> new RateNotFoundException(messageSource.getMessage(
                        AppConstants.RATE_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    @Override
    public List<RateReadDto> findByUserId(Long id) {
        return driverRateRepository.findByUserId(id)
                .stream()
                .map(rateMapper::toReadDto)
                .toList();
    }

    private void updateAverageRating(Long userId){
        List<RateReadDto> rateReadDtoList = findByUserId(userId);
        Double averageRating = rateCounterService.countRating(rateReadDtoList);
        UserRateDto userRatingDto = new UserRateDto(userId, averageRating);
        System.out.println(userRatingDto.averageRate());
        //rabbitService.sendMessage(EXCHANGE_NAME,DRIVER_ROUTING_KEY, userRatingDto);
    }
}

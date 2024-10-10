package org.example.rating.service;

import lombok.RequiredArgsConstructor;
import org.example.rating.constants.AppConstants;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.entity.Rate;
import org.example.rating.exception.rate.RateNotFoundException;
import org.example.rating.mapper.RateMapper;
import org.example.rating.repository.RateRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RateService {
    public final RateRepository rateRepository;
    public final RateMapper rateMapper;
    public final MessageSource messageSource;

    public Page<RateReadDto> findAll(Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);
        return rateRepository.findAll(request)
                .map(rateMapper::toReadDto);
    }

    public RateReadDto findById(Long id) {
        return rateRepository.findById(id)
                .map(rateMapper::toReadDto)
                .orElseThrow(() -> new RateNotFoundException(messageSource.getMessage(
                        AppConstants.RATE_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()
                )));
    }

    @Transactional
    public RateReadDto create(RateCreateEditDto rateDto) {
        Rate rate = rateMapper.toRate(rateDto); //TODO: fill field userId

        return rateMapper.toReadDto(rateRepository.save(rate));
    }

    @Transactional
    public RateReadDto update(Long id, RateCreateEditDto rateDto) {
        return rateRepository.findById(id)
                .map(rate -> {
                    rateMapper.map(rate, rateDto);
                    return rate;
                })
                .map(rateRepository::save)
                .map(rateMapper::toReadDto)
                .orElseThrow(() -> new RateNotFoundException(messageSource.getMessage(
                        AppConstants.RATE_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }
}

package org.example.rating.service;

import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.RateReadDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RateService {
    Page<RateReadDto> findAll(Integer page, Integer limit);
    RateReadDto findById(Long id);
    RateReadDto create(RateCreateEditDto rateDto);
    RateReadDto update(Long id, RateCreateEditDto rateDto);
    List<RateReadDto> findByUserId(Long id);

}

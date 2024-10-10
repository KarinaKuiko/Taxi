package org.example.rating.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.PageResponse;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.service.RateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rating")
@RequiredArgsConstructor
public class RateController {
    public final RateService rateService;

    @GetMapping
    public PageResponse<RateReadDto> findAll(@RequestParam(defaultValue = "0") Integer page,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return PageResponse.of(rateService.findAll(page, limit));
    }

    @GetMapping("/{id}")
    public RateReadDto findById(@PathVariable("id") Long id) {
        return rateService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RateReadDto create(@RequestBody @Valid RateCreateEditDto dto) {
        return rateService.create(dto);
    }

    @PutMapping("/{id}")
    public RateReadDto update(@PathVariable("id") Long id, @RequestBody @Valid RateCreateEditDto dto) {
        return rateService.update(id, dto);
    }
}

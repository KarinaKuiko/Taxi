package org.example.rating.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.PageResponse;
import org.example.rating.dto.read.RateReadDto;
import org.example.rating.entity.enumeration.UserType;
import org.example.rating.service.impl.DriverRateService;
import org.example.rating.service.impl.PassengerRateService;
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
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
public class RateController {
    public final DriverRateService driverRateService;
    public final PassengerRateService passengerRateService;

    @GetMapping("/driver")
    public PageResponse<RateReadDto> findAllDriversRates(@RequestParam(defaultValue = "0") Integer page,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return PageResponse.of(driverRateService.findAll(page, limit));
    }

    @GetMapping("/passenger")
    public PageResponse<RateReadDto> findAllPassengersRates(@RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return PageResponse.of(passengerRateService.findAll(page, limit));
    }

    @GetMapping("/driver/{id}")
    public RateReadDto findByDriverId(@PathVariable("id") Long id) {
        return driverRateService.findById(id);
    }

    @GetMapping("/passenger/{id}")
    public RateReadDto findByPassengerId(@PathVariable("id") Long id) {
        return passengerRateService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RateReadDto create(@RequestBody @Valid RateCreateEditDto dto) {
        return dto.userType() == UserType.PASSENGER ? driverRateService.create(dto) : passengerRateService.create(dto);
    }

    @PutMapping("/{id}")
    public RateReadDto update(@PathVariable("id") Long id, @RequestBody @Valid RateCreateEditDto dto) {
        return dto.userType() == UserType.PASSENGER ? driverRateService.update(id, dto) : passengerRateService.update(id, dto);
    }
}

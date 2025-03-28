package org.example.ride.controller.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.ride.controller.RideController;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.PageResponse;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
@Validated
public class RideControllerImpl implements RideController {
    private final RideService rideService;

    @GetMapping
    public PageResponse<RideReadDto> findAll(@RequestParam(name = "driverId", required = false) Long driverId,
                                             @RequestParam(name = "passengerId", required = false) Long passengerId,
                                             @RequestParam(defaultValue = "0") Integer page,
                                             @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return PageResponse.of(rideService.findRides(driverId, passengerId, page, limit));
    }

    @GetMapping("/driver-list")
    public List<RideReadDto> findTop100ByDriverId(@RequestParam(name = "driverId") Long driverId) {
        return rideService.findTop100ByDriverId(driverId);
    }

    @GetMapping("/{id}")
    public RideReadDto findById(@PathVariable("id") Long id) {
        return rideService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RideReadDto create(@RequestBody @Valid RideCreateEditDto dto) {
        return rideService.create(dto);
    }

    @PutMapping("/{id}")
    public RideReadDto update(@PathVariable("id") Long id, @RequestBody @Valid RideCreateEditDto dto) {
        return rideService.update(id, dto);
    }

    @PutMapping("/{id}/driver-status")
    public RideReadDto updateDriverStatus(@PathVariable("id") Long id, @RequestBody DriverRideStatusDto driverRideStatusDto) {
        return rideService.updateDriverStatus(id, driverRideStatusDto);
    }

    @PutMapping("/{id}/passenger-status")
    public RideReadDto updatePassengerStatus(@PathVariable("id") Long id, @RequestBody PassengerRideStatusDto passengerRideStatusDto) {
        return rideService.updatePassengerStatus(id, passengerRideStatusDto);
    }
}

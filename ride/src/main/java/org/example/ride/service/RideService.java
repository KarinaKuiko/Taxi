package org.example.ride.service;

import lombok.RequiredArgsConstructor;
import org.example.ride.client.DriverClient;
import org.example.ride.client.PassengerClient;
import org.example.ride.constants.AppConstants;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.create.RideStatusDto;
import org.example.ride.dto.read.RideReadDto;
import org.example.ride.entity.Ride;
import org.example.ride.entity.enumeration.RideStatus;
import org.example.ride.exception.param.InvalidCountParametersException;
import org.example.ride.exception.ride.RideNotFoundException;
import org.example.ride.mapper.RideMapper;
import org.example.ride.repository.RideRepository;
import org.example.ride.utils.PriceGenerator;
import org.example.ride.validation.RideStatusValidation;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {
    private final RideRepository rideRepository;
    private final MessageSource messageSource;
    private final RideMapper rideMapper;
    private final PriceGenerator priceGenerator;
    private final RideStatusValidation rideStatusValidation;
    private final PassengerClient passengerClient;
    private final DriverClient driverClient;

    public Page<RideReadDto> findRides(Long driverId, Long passengerId, Integer page, Integer limit) {
        if (driverId != null && passengerId != null) {
            throw new InvalidCountParametersException(messageSource.getMessage(
                    AppConstants.INVALID_COUNT_PARAMETERS,
                    new Object[]{},
                    LocaleContextHolder.getLocale()));
        }

        if (driverId != null) return findByDriverId(driverId, page, limit);
        if (passengerId != null) return findByPassengerId(passengerId, page, limit);

        return findAll(page, limit);
    }

    public Page<RideReadDto> findAll(Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);
        return rideRepository.findAll(request)
                .map(rideMapper::toReadDto);
    }

    public RideReadDto findById(Long id) {
        return rideRepository.findById(id)
                .map(rideMapper::toReadDto)
                .orElseThrow(() -> new RideNotFoundException(messageSource.getMessage(
                        AppConstants.RIDE_NOT_FOUND_EXCEPTION,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    public Page<RideReadDto> findByPassengerId(Long id, Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);
        return rideRepository.findByPassengerId(id, request)
                .map(rideMapper::toReadDto);
    }

    public Page<RideReadDto> findByDriverId(Long id, Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);
        return rideRepository.findByDriverId(id, request)
                .map(rideMapper::toReadDto);
    }

    @Transactional
    public RideReadDto create(RideCreateEditDto rideDto) {
        checkExistingDriver(rideDto.driverId());
        checkExistingPassenger(rideDto.passengerId());

        Ride ride = rideMapper.toRide(rideDto);
        ride.setRideStatus(RideStatus.CREATED);
        ride.setCost(priceGenerator.generateRandomCost());

        return rideMapper.toReadDto(rideRepository.save(ride));
    }

    @Transactional
    public RideReadDto update(Long id, RideCreateEditDto rideDto) {
        return rideRepository.findById(id)
                .map(ride -> {
                    checkExistingDriver(rideDto.driverId());
                    checkExistingPassenger(rideDto.passengerId());
                    rideMapper.map(ride, rideDto);
                    return ride;
                })
                .map(rideRepository::save)
                .map(rideMapper::toReadDto)
                .orElseThrow(() -> new RideNotFoundException(messageSource.getMessage(
                        AppConstants.RIDE_NOT_FOUND_EXCEPTION,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    @Transactional
    public RideReadDto updateStatus(Long id, RideStatusDto rideStatusDto) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(messageSource.getMessage(
                        AppConstants.RIDE_NOT_FOUND_EXCEPTION,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));

        rideStatusValidation.validateUpdatingStatus(ride, rideStatusDto);
        rideMapper.mapStatus(ride, rideStatusDto);

        return rideMapper.toReadDto(rideRepository.save(ride));
    }

    private void checkExistingDriver(Long id) {
        driverClient.findById(id);
    }

    private void checkExistingPassenger(Long id) {
        passengerClient.findById(id);
    }
}

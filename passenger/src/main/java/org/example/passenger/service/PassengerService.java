package org.example.passenger.service;

import lombok.RequiredArgsConstructor;
import org.example.passenger.constants.AppConstants;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.entity.Passenger;
import org.example.passenger.exception.passenger.DuplicatedPassengerEmailException;
import org.example.passenger.exception.passenger.PassengerNotFoundException;
import org.example.passenger.mapper.PassengerMapper;
import org.example.passenger.repository.PassengerRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private final MessageSource messageSource;

    public Page<PassengerReadDto> findAll(Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);

        return passengerRepository.findByIsDeletedFalse(request)
                .map(passengerMapper::toReadDto);
    }

    public Page<PassengerReadDto> findAllWithDeleted(Integer page, Integer limit) {
        Pageable request = PageRequest.of(page, limit);

        return passengerRepository.findAll(request)
                .map(passengerMapper::toReadDto);
    }

    public PassengerReadDto findById(Long id) {
        return passengerRepository.findByIdAndIsDeletedFalse(id)
                .map(passengerMapper::toReadDto)
                .orElseThrow(() -> new PassengerNotFoundException(messageSource.getMessage(
                        AppConstants.PASSENGER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND
                ));
    }

    @Transactional
    public PassengerReadDto create(PassengerCreateEditDto passengerDto) {
        passengerRepository.findByEmailAndIsDeletedFalse(passengerDto.email())
                .ifPresent(passenger -> {
                            throw new DuplicatedPassengerEmailException(messageSource.getMessage(
                                    AppConstants.PASSENGER_DUPlICATED_EMAIL,
                                    new Object[]{passengerDto.email()},
                                    LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
                        }
                    );

        Passenger passenger = passengerMapper.toPassenger(passengerDto);

        return passengerMapper.toReadDto(passengerRepository.save(passenger));
    }

    @Transactional
    public PassengerReadDto update(Long id, PassengerCreateEditDto passengerDto) {
        passengerRepository.findByEmailAndIsDeletedFalse(passengerDto.email())
                .ifPresent(passenger -> {
                    if(!passenger.getId().equals(id)) {
                        throw new DuplicatedPassengerEmailException(messageSource.getMessage(
                                AppConstants.PASSENGER_DUPlICATED_EMAIL,
                                new Object[]{passengerDto.email()},
                                LocaleContextHolder.getLocale()), HttpStatus.BAD_REQUEST);
                    }
                });

        return passengerRepository.findByIdAndIsDeletedFalse(id)
                .map(passenger -> {
                    passengerMapper.map(passenger, passengerDto);
                    return passenger;
                })
                .map(passengerRepository::save)
                .map(passengerMapper::toReadDto)
                .orElseThrow(() -> new PassengerNotFoundException(messageSource.getMessage(
                        AppConstants.PASSENGER_NOT_FOUND,
                        new Object[]{id},
                        LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND
                ));
    }

    @Transactional
    public void safeDelete(Long id) {
                passengerRepository.findByIdAndIsDeletedFalse(id)
                        .map(passenger -> {
                            passenger.setDeleted(true);
                            passengerRepository.save(passenger);
                            return passenger;
                        })
                        .orElseThrow(() -> new PassengerNotFoundException(messageSource.getMessage(
                                AppConstants.PASSENGER_NOT_FOUND,
                                new Object[]{id},
                                LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND
                        ));
    }
}

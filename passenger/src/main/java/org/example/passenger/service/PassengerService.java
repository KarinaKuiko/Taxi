package org.example.passenger.service;

import lombok.RequiredArgsConstructor;
import org.example.passenger.constants.AppConstants;
import org.example.passenger.dto.read.PassengerReadDto;
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
}

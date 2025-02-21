package org.example.passenger.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.passenger.constants.CommonConstants;
import org.example.passenger.constants.ExceptionConstants;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.dto.read.RideReadDto;
import org.example.passenger.dto.read.UserRateDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerService {
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;
    private final MessageSource messageSource;
    private final ImageStorageService imageStorageService;

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
                        ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
    }

    @Transactional
    public PassengerReadDto create(PassengerCreateEditDto passengerDto, MultipartFile multipartFile) {
        passengerRepository.findByEmailAndIsDeletedFalse(passengerDto.email())
                .ifPresent(passenger -> {
                            throw new DuplicatedPassengerEmailException(messageSource.getMessage(
                                    ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE,
                                    new Object[]{passengerDto.email()},
                                    LocaleContextHolder.getLocale()));
                        });

        Passenger passenger = passengerMapper.toPassenger(passengerDto);
        passenger.setRating(CommonConstants.DEFAULT_RATING);

        if (multipartFile != null) {
            String imageUrl = imageStorageService.uploadImage(multipartFile);
            passenger.setImageUrl(imageUrl);
        }

        return passengerMapper.toReadDto(passengerRepository.save(passenger));
    }

    @Transactional
    public PassengerReadDto update(Long id, PassengerCreateEditDto passengerDto, MultipartFile file) {
        return passengerRepository.findByIdAndIsDeletedFalse(id)
                .map(passenger -> {
                    passengerRepository.findByEmailAndIsDeletedFalse(passengerDto.email())
                            .ifPresent(passengerCheck -> {
                                if(!passengerCheck.getId().equals(id)) {
                                    throw new DuplicatedPassengerEmailException(messageSource.getMessage(
                                            ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE,
                                            new Object[]{passengerDto.email()},
                                            LocaleContextHolder.getLocale()));
                                }
                            });
                    passengerMapper.map(passenger, passengerDto);
                    String newImageUrl = imageStorageService.updateImage(passenger.getImageUrl(), file);
                    passenger.setImageUrl(newImageUrl);
                    return passenger;
                })
                .map(passengerRepository::save)
                .map(passengerMapper::toReadDto)
                .orElseThrow(() -> new PassengerNotFoundException(messageSource.getMessage(
                        ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                        new Object[]{id},
                        LocaleContextHolder.getLocale())));
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
                                ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                                new Object[]{id},
                                LocaleContextHolder.getLocale())));
    }

    public void notifyPassenger(RideReadDto rideReadDto) {
        log.info(rideReadDto.toString());
    }

    @Transactional
    public void updateRating(UserRateDto userRateDto) {
        Passenger passenger = passengerRepository.findById(userRateDto.userId()).get();
        passenger.setRating(userRateDto.averageRate());
        log.info("Update rating to {}, passenger id {}", userRateDto.averageRate(), userRateDto.userId());
        passengerRepository.save(passenger);
    }
}

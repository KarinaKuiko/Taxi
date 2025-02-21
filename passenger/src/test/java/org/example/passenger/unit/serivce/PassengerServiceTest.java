package org.example.passenger.unit.serivce;

import com.example.exceptionhandlerstarter.exception.passenger.DuplicatedPassengerEmailException;
import com.example.exceptionhandlerstarter.exception.passenger.PassengerNotFoundException;
import org.example.passenger.constants.ExceptionConstants;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.dto.read.UserRateDto;
import org.example.passenger.entity.Passenger;
import org.example.passenger.mapper.PassengerMapper;
import org.example.passenger.repository.PassengerRepository;
import org.example.passenger.service.ImageStorageService;
import org.example.passenger.service.PassengerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.example.passenger.util.DataUtil.DEFAULT_ID;
import static org.example.passenger.util.DataUtil.LIMIT_VALUE;
import static org.example.passenger.util.DataUtil.PAGE_VALUE;
import static org.example.passenger.util.DataUtil.getPassengerBuilder;
import static org.example.passenger.util.DataUtil.getPassengerCreateEditDtoBuilder;
import static org.example.passenger.util.DataUtil.getPassengerReadDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerServiceTest {

    @InjectMocks
    private PassengerService passengerService;

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PassengerMapper passengerMapper;

    @Mock
    private ImageStorageService imageStorageService;

    @Mock
    private MessageSource messageSource;

    @Test
    void findAll_thenReturnPagePassengerReadDto() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        Pageable request = PageRequest.of(PAGE_VALUE, LIMIT_VALUE);
        PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();

        when(passengerRepository.findByIsDeletedFalse(request)).thenReturn(
                new PageImpl<>(List.of(defaultPassenger), request, 1));
        when(passengerMapper.toReadDto(defaultPassenger)).thenReturn(readPassenger);

        Page<PassengerReadDto> result = passengerService.findAll(PAGE_VALUE, LIMIT_VALUE);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(passengerRepository).findByIsDeletedFalse(request);
        verify(passengerMapper).toReadDto(defaultPassenger);
    }

    @Test
    void findAllWithDeleted_thenReturnPagePassengerReadDto() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        Pageable request = PageRequest.of(PAGE_VALUE, LIMIT_VALUE);
        PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();

        when(passengerRepository.findAll(request)).thenReturn(
                new PageImpl<>(List.of(defaultPassenger), request, 1));
        when(passengerMapper.toReadDto(defaultPassenger)).thenReturn(readPassenger);

        Page<PassengerReadDto> result = passengerService.findAllWithDeleted(PAGE_VALUE, LIMIT_VALUE);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(passengerRepository).findAll(request);
        verify(passengerMapper).toReadDto(defaultPassenger);
    }

    @Test
    void findById_whenPassengerIsFound_thenReturnPassengerReadDto() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();

        when(passengerRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultPassenger));
        when(passengerMapper.toReadDto(defaultPassenger)).thenReturn(readPassenger);

        assertThat(passengerService.findById(DEFAULT_ID)).isNotNull();
        verify(passengerRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(passengerMapper).toReadDto(defaultPassenger);
    }

    @Test
    void findById_whenPassengerIsNotFound_thenThrowPassengerNotFoundException() {
        when(passengerRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE);

        PassengerNotFoundException exception = assertThrows(PassengerNotFoundException.class,
                () -> passengerService.findById(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE);
        verify(messageSource).getMessage(
                ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(passengerRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(passengerMapper, never()).toReadDto(any());
    }

    @Test
    void create_whenEmailIsNotDuplicated_thenReturnPassengerReadDto() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
        PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();

        when(passengerRepository.findByEmailAndIsDeletedFalse(createPassenger.email()))
                .thenReturn(Optional.empty());
        when(passengerMapper.toPassenger(createPassenger)).thenReturn(defaultPassenger);
        when(passengerRepository.save(defaultPassenger)).thenReturn(defaultPassenger);
        when(passengerMapper.toReadDto(defaultPassenger)).thenReturn(readPassenger);

        assertThat(passengerService.create(createPassenger, null)).isNotNull();
        verify(passengerRepository).findByEmailAndIsDeletedFalse(createPassenger.email());
        verify(passengerMapper).toPassenger(createPassenger);
        verify(passengerRepository).save(defaultPassenger);
        verify(passengerMapper).toReadDto(defaultPassenger);
    }

    @Test
    void create_whenEmailIsDuplicated_thenThrowDuplicatedPassengerEmailException() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();

        when(passengerRepository.findByEmailAndIsDeletedFalse(createPassenger.email()))
                .thenReturn(Optional.of(defaultPassenger));
        when(messageSource.getMessage(
                ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE,
                new Object[]{createPassenger.email()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE);

        DuplicatedPassengerEmailException exception = assertThrows(DuplicatedPassengerEmailException.class,
                () -> passengerService.create(createPassenger, null));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE);
        verify(messageSource).getMessage(
                ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE,
                new Object[]{createPassenger.email()},
                LocaleContextHolder.getLocale());
        verify(passengerRepository).findByEmailAndIsDeletedFalse(createPassenger.email());
        verify(passengerMapper, never()).toPassenger(any());
        verify(passengerRepository, never()).save(any());
        verify(passengerMapper, never()).toReadDto(any());
    }

    @Test
    void update_whenEmailIsDuplicatedWithSameIds_thenReturnPassengerReadDto() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();
        PassengerReadDto readPassenger = getPassengerReadDtoBuilder().build();

        when(passengerRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultPassenger));
        when(passengerRepository.findByEmailAndIsDeletedFalse(createPassenger.email()))
                .thenReturn(Optional.of(defaultPassenger));
        when(imageStorageService.updateImage(null, null)).thenReturn(null);
        when(passengerRepository.save(defaultPassenger)).thenReturn(defaultPassenger);
        when(passengerMapper.toReadDto(defaultPassenger)).thenReturn(readPassenger);

        assertThat(passengerService.update(DEFAULT_ID, createPassenger, null)).isNotNull();
        verify(passengerRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(passengerRepository).findByEmailAndIsDeletedFalse(createPassenger.email());
        verify(passengerMapper).map(defaultPassenger, createPassenger);
        verify(passengerRepository).save(defaultPassenger);
        verify(passengerMapper).toReadDto(defaultPassenger);
    }

    @Test
    void update_whenEmailIsDuplicatedWithDifferentIds_thenThrowDuplicatedPassengerEmailException() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();

        when(passengerRepository.findByIdAndIsDeletedFalse(2L)).thenReturn(Optional.of(defaultPassenger));
        when(passengerRepository.findByEmailAndIsDeletedFalse(createPassenger.email()))
                .thenReturn(Optional.of(defaultPassenger));
        when(messageSource.getMessage(
                ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE,
                new Object[]{createPassenger.email()},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE);

        DuplicatedPassengerEmailException exception = assertThrows(DuplicatedPassengerEmailException.class,
                () -> passengerService.update(2L, createPassenger, null));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE);
        verify(passengerRepository).findByEmailAndIsDeletedFalse(createPassenger.email());
        verify(passengerMapper, never()).map(any(), any());
        verify(messageSource).getMessage(
                ExceptionConstants.PASSENGER_DUPlICATED_EMAIL_MESSAGE,
                new Object[]{createPassenger.email()},
                LocaleContextHolder.getLocale());
        verify(passengerRepository, never()).save(any());
        verify(passengerMapper, never()).toReadDto(any());
    }

    @Test
    void update_whenPassengerIsNotFound_thenThrowPassengerNotFoundException() {
        PassengerCreateEditDto createPassenger = getPassengerCreateEditDtoBuilder().build();

        when(passengerRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE);

        PassengerNotFoundException exception = assertThrows(PassengerNotFoundException.class,
                () -> passengerService.update(DEFAULT_ID, createPassenger, null));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE);
        verify(passengerRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(passengerRepository, never()).findByEmailAndIsDeletedFalse(any());
        verify(passengerMapper, never()).map(any(), any());
        verify(passengerRepository, never()).save(any());
        verify(passengerMapper, never()).toReadDto(any());
        verify(messageSource).getMessage(
                ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
    }

    @Test
    void safeDelete_whenPassengerIsFound_thenMarkAsDeleted() {
        Passenger defaultPassenger = getPassengerBuilder().build();

        when(passengerRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.of(defaultPassenger));
        when(passengerRepository.save(defaultPassenger)).thenReturn(defaultPassenger);

        passengerService.safeDelete(DEFAULT_ID);

        assertThat(defaultPassenger.isDeleted()).isTrue();
        verify(passengerRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(passengerRepository).save(defaultPassenger);
    }

    @Test
    void safeDelete_whenPassengerIsNotFound_thenThrowPassengerNotFoundException() {
        when(passengerRepository.findByIdAndIsDeletedFalse(DEFAULT_ID)).thenReturn(Optional.empty());
        when(messageSource.getMessage(
                ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale()))
                .thenReturn(ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE);

        PassengerNotFoundException exception = assertThrows(PassengerNotFoundException.class,
                () -> passengerService.safeDelete(DEFAULT_ID));

        assertThat(exception.getMessage()).isEqualTo(ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE);
        verify(passengerRepository).findByIdAndIsDeletedFalse(DEFAULT_ID);
        verify(messageSource).getMessage(
                ExceptionConstants.PASSENGER_NOT_FOUND_MESSAGE,
                new Object[]{DEFAULT_ID},
                LocaleContextHolder.getLocale());
        verify(passengerRepository, never()).save(any());
    }

    @Test
    void updateRating_thenUpdateRating() {
        Passenger defaultPassenger = getPassengerBuilder().build();
        UserRateDto rateDto = new UserRateDto(DEFAULT_ID, 4.0);

        when(passengerRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(defaultPassenger));
        when(passengerRepository.save(defaultPassenger)).thenReturn(defaultPassenger);

        passengerService.updateRating(rateDto);

        assertThat(defaultPassenger.getRating()).isEqualTo(4.0);
        verify(passengerRepository).findById(DEFAULT_ID);
        verify(passengerRepository).save(defaultPassenger);
    }
}

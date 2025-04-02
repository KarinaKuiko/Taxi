package com.example.exceptionhandlerstarter.exception;

import com.example.exceptionhandlerstarter.constants.ExceptionConstants;
import com.example.exceptionhandlerstarter.dto.ExceptionDto;
import com.example.exceptionhandlerstarter.dto.ValidationResponse;
import com.example.exceptionhandlerstarter.dto.Violation;
import com.example.exceptionhandlerstarter.exception.car.CarNotFoundException;
import com.example.exceptionhandlerstarter.exception.car.DuplicatedCarNumberException;
import com.example.exceptionhandlerstarter.exception.driver.DriverNotFoundException;
import com.example.exceptionhandlerstarter.exception.driver.DuplicatedDriverEmailException;
import com.example.exceptionhandlerstarter.exception.minio.AvatarNotFoundException;
import com.example.exceptionhandlerstarter.exception.minio.FileDeleteException;
import com.example.exceptionhandlerstarter.exception.minio.FileUploadException;
import com.example.exceptionhandlerstarter.exception.param.InvalidCountParametersException;
import com.example.exceptionhandlerstarter.exception.passenger.DuplicatedPassengerEmailException;
import com.example.exceptionhandlerstarter.exception.passenger.PassengerNotFoundException;
import com.example.exceptionhandlerstarter.exception.rate.RateNotFoundException;
import com.example.exceptionhandlerstarter.exception.ride.CanceledRideStatusException;
import com.example.exceptionhandlerstarter.exception.ride.InvalidRideStatusForChangingException;
import com.example.exceptionhandlerstarter.exception.ride.IrrelevantDriverStatusException;
import com.example.exceptionhandlerstarter.exception.ride.RideException;
import com.example.exceptionhandlerstarter.exception.ride.RideNotFoundException;
import com.example.exceptionhandlerstarter.exception.security.AccessDeniedException;
import com.example.exceptionhandlerstarter.keycloak.ClientException;
import com.example.exceptionhandlerstarter.keycloak.KeycloakException;
import com.example.exceptionhandlerstarter.keycloak.UnauthorizedException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleCarNotFoundException(CarNotFoundException exception) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(DuplicatedCarNumberException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleDuplicatedCarNumberException(DuplicatedCarNumberException exception) {
        return new ExceptionDto(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(DriverNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleDriverNotFoundException(DriverNotFoundException exception) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(DuplicatedDriverEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleDuplicatedDriverEmailException(DuplicatedDriverEmailException exception) {
        return new ExceptionDto(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(DuplicatedPassengerEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleDuplicatedPassengerEmailException(DuplicatedPassengerEmailException exception) {
        return new ExceptionDto(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(PassengerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handlePassengerNotFoundException(PassengerNotFoundException exception) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RideException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleRideException(RideException e) {
        ExceptionDto exception = e.getExceptionDto();
        return new ExceptionDto(exception.status(), exception.message(), exception.time());
    }

    @ExceptionHandler(RateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleRateNotFoundException(RateNotFoundException exception) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RideNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleRideNotFoundException(RideNotFoundException exception) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(ClientException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleClientException(ClientException e) {
        ExceptionDto exception = e.getExceptionDto();
        return new ExceptionDto(exception.status(), exception.message(), exception.time());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionDto handleServiceUnavailableException(ServiceUnavailableException e) {
        return new ExceptionDto(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ExceptionDto handleUnauthorizedException(UnauthorizedException e) {
        return new ExceptionDto(HttpStatus.UNAUTHORIZED, e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(KeycloakException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionDto handleKeycloakException(KeycloakException e) {
        ExceptionDto exception = e.getExceptionDto();
        return new ExceptionDto(exception.status(), exception.message(), exception.time());
    }

    @ExceptionHandler(IrrelevantDriverStatusException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleIrrelevantDriverStatusException(IrrelevantDriverStatusException exception) {
        return new ExceptionDto(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(InvalidCountParametersException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto handleInvalidCountParametersException(InvalidCountParametersException exception) {
        return new ExceptionDto(HttpStatus.BAD_REQUEST, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(CanceledRideStatusException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleCanceledRideStatusException(CanceledRideStatusException exception) {
        return new ExceptionDto(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(InvalidRideStatusForChangingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleInvalidRideStatusForChangingException(InvalidRideStatusForChangingException exception) {
        return new ExceptionDto(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AvatarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleAvatarNotFoundException(AvatarNotFoundException exception) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionDto handleFileUploadException(FileUploadException exception) {
        return new ExceptionDto(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(FileDeleteException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ExceptionDto handleFileDeleteException(FileDeleteException exception) {
        return new ExceptionDto(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionDto handleAccessDeniedException(AccessDeniedException exception) {
        return new ExceptionDto(HttpStatus.FORBIDDEN, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleIOException(IOException exception) {
        return new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleRuntimeException(RuntimeException exception) {
        exception.printStackTrace();
        return new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionConstants.INTERNAL_SERVER_ERROR, LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<Violation> violations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> new Violation(e.getField(), e.getDefaultMessage()))
                .toList();
        return new ValidationResponse(violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResponse handleConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations()
                .stream()
                .map(violation -> new Violation(
                                violation.getPropertyPath().toString().replaceFirst(".*\\.", ""),
                                violation.getMessage()))
                .toList();
        return new ValidationResponse(violations);
    }
}

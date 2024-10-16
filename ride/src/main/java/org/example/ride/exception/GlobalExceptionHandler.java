package org.example.ride.exception;

import jakarta.validation.ConstraintViolationException;
import org.example.ride.constants.AppConstants;
import org.example.ride.dto.read.ExceptionDto;
import org.example.ride.dto.read.ValidationResponse;
import org.example.ride.exception.param.InvalidCountParametersException;
import org.example.ride.exception.ride.CanceledRideStatusException;
import org.example.ride.exception.ride.InvalidRideStatusForChangingException;
import org.example.ride.exception.ride.RideNotFoundException;
import org.example.ride.exception.violation.Violation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(RideNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDto handleRideNotFoundException(RideNotFoundException exception) {
        return new ExceptionDto(HttpStatus.NOT_FOUND, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(InvalidRideStatusForChangingException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDto handleInvalidRideStatusForChangingException(InvalidRideStatusForChangingException exception) {
        return new ExceptionDto(HttpStatus.CONFLICT, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleRuntimeException(RuntimeException exception) {
        return new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, AppConstants.INTERNAL_SERVER_ERROR, LocalDateTime.now());
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
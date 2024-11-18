package org.example.driver.exception;

import jakarta.validation.ConstraintViolationException;
import org.example.driver.constants.ExceptionConstants;
import org.example.driver.dto.read.ExceptionDto;
import org.example.driver.dto.read.ValidationResponse;
import org.example.driver.exception.car.CarNotFoundException;
import org.example.driver.exception.car.DuplicatedCarNumberException;
import org.example.driver.exception.driver.DriverNotFoundException;
import org.example.driver.exception.driver.DuplicatedDriverEmailException;
import org.example.driver.exception.violation.Violation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleRuntimeException(RuntimeException exception) {
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

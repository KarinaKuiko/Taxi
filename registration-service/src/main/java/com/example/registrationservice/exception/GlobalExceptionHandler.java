package com.example.registrationservice.exception;

import com.example.registrationservice.constants.ExceptionConstants;
import com.example.registrationservice.dto.read.ExceptionDto;
import com.example.registrationservice.dto.read.ValidationResponse;
import com.example.registrationservice.exception.keycloak.UnauthorizedException;
import com.example.registrationservice.exception.violation.Violation;
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

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ExceptionDto> handleClientException(ClientException e) {
        ExceptionDto exceptionDto = e.getExceptionDto();
        return ResponseEntity.status(exceptionDto.status()).body(exceptionDto);
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
    public ResponseEntity<ExceptionDto> handleKeycloakException(KeycloakException e) {
        ExceptionDto exceptionDto = e.getExceptionDto();
        return ResponseEntity.status(exceptionDto.status()).body(exceptionDto);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleIOException(IOException exception) {
        return new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionDto handleRuntimeException(RuntimeException exception) {
        return new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionConstants.INTERNAL_SERVER_ERROR_MESSAGE, LocalDateTime.now());
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

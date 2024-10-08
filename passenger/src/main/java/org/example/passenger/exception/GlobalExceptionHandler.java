package org.example.passenger.exception;

import org.example.passenger.exception.passenger.DuplicatedPassengerEmailException;
import org.example.passenger.exception.passenger.PassengerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({DuplicatedPassengerEmailException.class})
    public ResponseEntity<Object> handleDuplicatedPassengerEmailException(DuplicatedPassengerEmailException exception) {
        return ResponseEntity.status(exception.getStatus())
                             .body(exception.getMessage());
    }

    @ExceptionHandler({PassengerNotFoundException.class})
    public ResponseEntity<Object> handlePassengerNotFoundException(PassengerNotFoundException exception) {
        return ResponseEntity.status(exception.getStatus())
                             .body(exception.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}

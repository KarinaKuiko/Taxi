package org.example.driver.exception;

import org.example.driver.exception.car.CarNotFoundException;
import org.example.driver.exception.car.DuplicatedCarNumberException;
import org.example.driver.exception.driver.DriverNotFoundException;
import org.example.driver.exception.driver.DuplicatedDriverEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CarNotFoundException.class})
    public ResponseEntity<Object> handleCarNotFoundException(CarNotFoundException exception) {
        return ResponseEntity.status(exception.getStatus())
                             .body(exception.getMessage());
    }

    @ExceptionHandler({DuplicatedCarNumberException.class})
    public ResponseEntity<Object> handleDuplicatedCarNumberException(DuplicatedCarNumberException exception) {
        return ResponseEntity.status(exception.getStatus())
                             .body(exception.getMessage());
    }

    @ExceptionHandler({DriverNotFoundException.class})
    public ResponseEntity<Object> handleDriverNotFoundException(DriverNotFoundException exception) {
        return ResponseEntity.status(exception.getStatus())
                             .body(exception.getMessage());
    }

    @ExceptionHandler({DuplicatedDriverEmailException.class})
    public ResponseEntity<Object> handleDuplicatedDriverEmailException(DuplicatedDriverEmailException exception) {
        return ResponseEntity.status(exception.getStatus())
                             .body(exception.getMessage());
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(exception.getMessage());
    }
}

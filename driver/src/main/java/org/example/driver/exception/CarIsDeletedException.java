package org.example.driver.exception;

import org.springframework.http.HttpStatus;

public class CarIsDeletedException extends BaseException {

    public CarIsDeletedException(String message, HttpStatus status) {
        super(message, status);
    }
}

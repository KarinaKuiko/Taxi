package org.example.driver.exception.car;

import org.example.driver.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CarNotFoundException extends BaseException {

    public CarNotFoundException(String message, HttpStatus status) {
        super(message, status);
    }
}

package org.example.driver.exception.driver;

import org.example.driver.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DriverNotFoundException extends BaseException {
    public DriverNotFoundException(String message) {
        super(message);
    }
}

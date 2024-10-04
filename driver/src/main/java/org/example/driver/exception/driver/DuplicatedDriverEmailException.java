package org.example.driver.exception.driver;

import org.example.driver.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicatedDriverEmailException extends BaseException {
    public DuplicatedDriverEmailException(String message, HttpStatus status) {
        super(message, status);
    }
}

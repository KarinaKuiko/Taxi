package org.example.driver.exception.car;

import org.example.driver.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicatedCarNumberException extends BaseException {
    public DuplicatedCarNumberException(String message, HttpStatus status) {
        super(message, status);
    }
}

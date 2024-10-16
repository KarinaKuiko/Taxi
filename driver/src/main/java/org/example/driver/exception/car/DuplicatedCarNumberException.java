package org.example.driver.exception.car;

import org.example.driver.exception.BaseException;

public class DuplicatedCarNumberException extends BaseException {
    public DuplicatedCarNumberException(String message) {
        super(message);
    }
}

package org.example.driver.exception.car;

import org.example.driver.exception.BaseException;

public class CarNotFoundException extends BaseException {

    public CarNotFoundException(String message) {
        super(message);
    }
}

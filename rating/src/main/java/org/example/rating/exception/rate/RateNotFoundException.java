package org.example.rating.exception.rate;

import org.example.rating.exception.BaseException;

public class RateNotFoundException extends BaseException {
    public RateNotFoundException(String message) {
        super(message);
    }
}

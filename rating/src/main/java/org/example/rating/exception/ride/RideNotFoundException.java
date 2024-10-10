package org.example.rating.exception.ride;

import org.example.rating.exception.BaseException;

public class RideNotFoundException extends BaseException {
    public RideNotFoundException(String message) {
        super(message);
    }
}

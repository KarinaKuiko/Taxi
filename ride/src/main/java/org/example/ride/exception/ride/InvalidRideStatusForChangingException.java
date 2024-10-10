package org.example.ride.exception.ride;

import org.example.ride.exception.BaseException;

public class InvalidRideStatusForChangingException extends BaseException {
    public InvalidRideStatusForChangingException(String message) {
        super(message);
    }
}

package org.example.passenger.exception.passenger;

import org.example.passenger.exception.BaseException;

public class DuplicatedPassengerEmailException extends BaseException {
    public DuplicatedPassengerEmailException(String message) {
        super(message);
    }
}

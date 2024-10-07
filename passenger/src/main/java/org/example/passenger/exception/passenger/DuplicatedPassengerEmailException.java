package org.example.passenger.exception.passenger;

import org.example.passenger.exception.BaseException;
import org.springframework.http.HttpStatus;

public class DuplicatedPassengerEmailException extends BaseException {
    public DuplicatedPassengerEmailException(String message, HttpStatus status) {
        super(message, status);
    }
}

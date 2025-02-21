package com.example.exceptionhandlerstarter.exception.passenger;


import com.example.exceptionhandlerstarter.exception.BaseException;

public class DuplicatedPassengerEmailException extends BaseException {
    public DuplicatedPassengerEmailException(String message) {
        super(message);
    }
}

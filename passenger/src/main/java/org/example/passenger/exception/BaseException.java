package org.example.passenger.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException {

    public BaseException(String message) {
        super(message);
    }
}

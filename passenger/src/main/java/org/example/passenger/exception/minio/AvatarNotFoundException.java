package org.example.passenger.exception.minio;

import org.example.passenger.exception.BaseException;

public class AvatarNotFoundException extends BaseException {
    public AvatarNotFoundException(String message) {
        super(message);
    }
}

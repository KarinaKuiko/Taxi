package com.example.exceptionhandlerstarter.exception.minio;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class AvatarNotFoundException extends BaseException {
    public AvatarNotFoundException(String message) {
        super(message);
    }
}

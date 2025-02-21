package org.example.driver.exception.minio;

import org.example.driver.exception.BaseException;

public class AvatarNotFoundException extends BaseException {
    public AvatarNotFoundException(String message) {
        super(message);
    }
}

package com.example.exceptionhandlerstarter.exception.minio;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class FileDeleteException extends BaseException {
    public FileDeleteException(String message) {
        super(message);
    }
}

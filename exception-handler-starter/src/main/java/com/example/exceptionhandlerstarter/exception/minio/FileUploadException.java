package com.example.exceptionhandlerstarter.exception.minio;

import com.example.exceptionhandlerstarter.exception.BaseException;

public class FileUploadException extends BaseException {
    public FileUploadException(String message) {
        super(message);
    }
}

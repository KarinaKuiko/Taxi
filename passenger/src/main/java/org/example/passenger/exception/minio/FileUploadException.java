package org.example.passenger.exception.minio;

import org.example.passenger.exception.BaseException;

public class FileUploadException extends BaseException {
    public FileUploadException(String message) {
        super(message);
    }
}

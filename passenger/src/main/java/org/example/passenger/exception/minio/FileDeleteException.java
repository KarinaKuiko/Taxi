package org.example.passenger.exception.minio;

import org.example.passenger.exception.BaseException;

public class FileDeleteException extends BaseException {
    public FileDeleteException(String message) {
        super(message);
    }
}

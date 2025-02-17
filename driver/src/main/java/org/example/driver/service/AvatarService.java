package org.example.driver.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.driver.constants.ExceptionConstants;
import org.example.driver.dto.read.MinioAvatar;
import org.example.driver.exception.minio.AvatarNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final MinioClient minioClient;
    private final MessageSource messageSource;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @SneakyThrows
    public MinioAvatar getAvatar(String name) {
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build());
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(name)
                            .build());
            return new MinioAvatar(inputStream, MediaType.valueOf(statObjectResponse.contentType()));
        } catch (ErrorResponseException e) {
            throw new AvatarNotFoundException(messageSource.getMessage(
                    ExceptionConstants.AVATAR_NOT_FOUND,
                    new Object[]{name},
                    LocaleContextHolder.getLocale()));
        }
    }
}

package org.example.passenger.service;

import com.example.exceptionhandlerstarter.exception.minio.AvatarNotFoundException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.passenger.constants.ExceptionConstants;
import org.example.passenger.dto.read.MinioAvatar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

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

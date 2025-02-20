package org.example.passenger.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.passenger.constants.ExceptionConstants;
import org.example.passenger.exception.minio.FileDeleteException;
import org.example.passenger.exception.minio.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final MinioClient minioClient;
    private final MessageSource messageSource;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadImage(MultipartFile file) {
        String fileName = generateFileName(file);
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return bucketName + "/" + fileName;
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new FileUploadException(messageSource.getMessage(
                    ExceptionConstants.FILE_UPLOAD_EXCEPTION,
                    new Object[]{fileName},
                    LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateImage(String oldImageUrl, MultipartFile file) {
        if (oldImageUrl != null) {
            deleteImage(oldImageUrl);
        }

        return (file != null) ? uploadImage(file) : null;
    }

    public void deleteImage(String imageUrl) {
        String imageName = imageUrl.substring(imageUrl.indexOf(bucketName + "/") + bucketName.length() + 1);

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(imageName).build());
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new FileDeleteException(messageSource.getMessage(
                    ExceptionConstants.FILE_DELETE_EXCEPTION,
                    new Object[]{},
                    LocaleContextHolder.getLocale()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateFileName(MultipartFile file) {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(Instant.now());
        return timestamp + "-" + Objects.requireNonNull(file.getOriginalFilename()).replace(" ", "_");
    }
}

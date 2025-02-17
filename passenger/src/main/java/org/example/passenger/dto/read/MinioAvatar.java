package org.example.passenger.dto.read;

import org.springframework.http.MediaType;

import java.io.InputStream;

public record MinioAvatar(
        InputStream inputStream,
        MediaType mediaType
) {
}

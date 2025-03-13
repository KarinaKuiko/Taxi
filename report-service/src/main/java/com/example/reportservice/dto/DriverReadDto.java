package com.example.reportservice.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record DriverReadDto(
        long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String gender,
        Long carId,
        double rating,
        String imageUrl
) implements Serializable {
}

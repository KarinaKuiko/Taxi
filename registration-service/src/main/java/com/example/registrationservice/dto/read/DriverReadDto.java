package com.example.registrationservice.dto.read;

public record DriverReadDto(
        long id,
        String firstName,
        String secondName,
        String email,
        String phone,
        String gender,
        Long carId,
        double rating,
        String imageUrl
) {
}

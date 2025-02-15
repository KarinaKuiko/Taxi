package com.example.registrationservice.dto.read;

public record PassengerReadDto(
        long id,
        String firstName,
        String secondName,
        String email,
        String phone,
        double rating,
        String imageUrl
) {
}

package com.example.registrationservice.exception.violation;

public record Violation(
        String field,
        String message
) {
}
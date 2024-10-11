package org.example.passenger.exception.violation;

public record Violation(
        String field,
        String message
) {
}
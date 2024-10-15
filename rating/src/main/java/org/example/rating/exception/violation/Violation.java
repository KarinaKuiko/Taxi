package org.example.rating.exception.violation;

public record Violation(
        String field,
        String message
) {
}

package org.example.passenger.dto.read;

import org.example.passenger.exception.violation.Violation;

import java.util.List;

public record ValidationResponse(
        List<Violation> violations
) {
}
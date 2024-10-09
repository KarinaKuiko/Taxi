package org.example.ride.dto.read;

import org.example.ride.exception.violation.Violation;

import java.util.List;

public record ValidationResponse(
        List<Violation> violations
) {
}

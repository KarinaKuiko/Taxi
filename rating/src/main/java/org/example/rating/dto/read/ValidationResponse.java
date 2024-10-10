package org.example.rating.dto.read;

import org.example.rating.exception.violation.Violation;

import java.util.List;

public record ValidationResponse(
        List<Violation> violations
) {
}

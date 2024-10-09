package org.example.driver.dto.read;

import org.example.driver.exception.violation.Violation;

import java.util.List;

public record ValidationResponse(
        List<Violation> violations
) {
}

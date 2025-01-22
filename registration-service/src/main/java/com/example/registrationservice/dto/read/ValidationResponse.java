package com.example.registrationservice.dto.read;


import com.example.registrationservice.exception.violation.Violation;

import java.util.List;

public record ValidationResponse(
        List<Violation> violations
) {
}
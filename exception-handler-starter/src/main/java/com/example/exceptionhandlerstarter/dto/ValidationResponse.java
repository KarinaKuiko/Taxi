package com.example.exceptionhandlerstarter.dto;

import java.util.List;

public record ValidationResponse(
        List<Violation> violations
) {
}

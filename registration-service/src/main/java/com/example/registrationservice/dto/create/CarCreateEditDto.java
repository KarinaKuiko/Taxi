package com.example.registrationservice.dto.create;

import com.example.registrationservice.constants.AppConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record CarCreateEditDto(
        String color,
        String brand,

        @Pattern(message = "{number.invalid}",
                regexp = AppConstants.CAR_NUMBER_PATTERN)
        String number,

        @Min(value = 1980, message = "{year.invalid}")
        @Max(value = 2025, message = "{year.invalid}")
        Integer year
) {
}

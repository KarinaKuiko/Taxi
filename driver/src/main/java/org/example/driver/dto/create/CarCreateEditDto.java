package org.example.driver.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.example.driver.constants.AppConstants;

public record CarCreateEditDto(
        @NotBlank(message = "{color.blank}")
        String color,

        @NotBlank(message = "{brand.blank}")
        String brand,

        @NotBlank(message = "{number.blank}")
        @Pattern(message = "{number.invalid}",
                regexp = AppConstants.CAR_NUMBER_PATTERN)
        String number,

        @NotNull(message = "{year.null}")
        @Min(value = 1980, message = "{year.invalid}")
        @Max(value = 2025, message = "{year.invalid}")
        Integer year
) {
}

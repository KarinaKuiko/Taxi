package org.example.driver.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CarCreateEditDto(
        @NotBlank(message = "{color.blank}")
        String color,

        @NotBlank(message = "{brand.blank}")
        String brand,

        @NotBlank(message = "{number.blank}")
        @Pattern(message = "{number.invalid}",
                regexp = "^[A-Z]{1,2}[0-9]{3}[A-Z]{2,3}$|^[A-Z]{1,2}-[0-9]{3}-[A-Z]{2,3}$|" +
                        "^[0-9]{3}-[A-Z]{1,2}-[0-9]{2}$|^[A-Z]{2}-[0-9]{3}-[A-Z]{1,2}$")
        String number,

        @NotNull(message = "{year.null}")
        @Min(value = 1980, message = "{year.invalid}")
        @Max(value = 2025, message = "{year.invalid}")
        Integer year
) {
}

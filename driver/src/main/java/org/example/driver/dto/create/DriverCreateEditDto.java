package org.example.driver.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.example.driver.constants.AppConstants;
import org.example.driver.entity.enumeration.Gender;

public record DriverCreateEditDto(

        @NotBlank(message = "{name.blank}")
        String name,

        @Email(message = "{email.invalid}")
        @NotBlank(message = "{email.blank}")
        String email,

        @Pattern(message = "{phone.invalid}",
                regexp = AppConstants.PHONE_PATTERN)
        @NotBlank(message = "{phone.blank}")
        String phone,

        Gender gender,
        Long carId
) {
}

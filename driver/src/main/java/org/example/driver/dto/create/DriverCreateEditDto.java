package org.example.driver.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.example.driver.constants.ExceptionConstants;
import org.example.driver.entity.enumeration.Gender;

@Builder
public record DriverCreateEditDto(

        @NotBlank(message = "{name.blank}")
        String firstName,

        @NotBlank(message = "{name.blank}")
        String lastName,

        @Email(message = "{email.invalid}")
        @NotBlank(message = "{email.blank}")
        String email,

        @Pattern(message = "{phone.invalid}",
                regexp = ExceptionConstants.PHONE_PATTERN)
        @NotBlank(message = "{phone.blank}")
        String phone,

        Gender gender,
        CarCreateEditDto carCreateEditDto
) {
}

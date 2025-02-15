package com.example.registrationservice.dto.create;

import com.example.registrationservice.constants.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

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
                regexp = AppConstants.PHONE_PATTERN)
        @NotBlank(message = "{phone.blank}")
        String phone,

        String gender,
        CarCreateEditDto carCreateEditDto
) {
}

package com.example.registrationservice.dto.create;

import com.example.registrationservice.constants.AppConstants;
import com.example.registrationservice.enumeration.Gender;
import com.example.registrationservice.enumeration.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpDto (

        String username,
        @NotBlank(message = "{firstname.empty}")
        String firstName,

        @NotBlank(message = "{secondname.empty}")
        String lastName,

        @NotBlank(message = "{email.blank}")
        @Email(message = "{email.invalid}")
        String email,

        @NotBlank(message = "{password.empty}")
        String password,

        @Pattern(message = "{phone.invalid}",
                regexp = AppConstants.PHONE_PATTERN)
        @NotBlank(message = "{phone.blank}")
        String phone,
        Gender gender,
        Role role,
        CarCreateEditDto carCreateEditDto
) {
}

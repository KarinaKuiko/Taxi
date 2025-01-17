package com.example.registrationservice.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInUserDto (

        @Email(message = "{email.invalid}")
        @NotBlank(message = "{email.blank}")
        String email,

        @NotBlank(message = "{password.empty}")
        String password
) {
}

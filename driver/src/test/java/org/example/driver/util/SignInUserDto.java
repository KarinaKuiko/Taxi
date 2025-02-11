package org.example.driver.util;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInUserDto (
        String email,
        String password
) {
}

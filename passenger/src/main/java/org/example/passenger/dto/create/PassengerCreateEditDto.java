package org.example.passenger.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PassengerCreateEditDto (

        @NotBlank(message = "{name.blank}")
        String name,

        @NotBlank(message = "{email.blank}")
        @Email(message = "{email.invalid}")
        String email,

        @Pattern(message = "{phone.invalid}",
                regexp = "^(\\+375(29|33|44|25|17|16|15|44|29|33|44)?[0-9]{7}|80(29|33|44|25|17|16|15|44)?[0-9]{7})$")
        @NotBlank(message = "{phone.blank}")
        String phone
) {
}

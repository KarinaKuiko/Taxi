package org.example.passenger.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.example.passenger.constants.ExceptionConstants;
import org.example.passenger.entity.enumeration.Gender;

@Builder
public record PassengerCreateEditDto (

        @NotBlank(message = "{name.blank}")
        String firstName,

        @NotBlank(message = "{name.blank}")
        String lastName,

        @NotBlank(message = "{email.blank}")
        @Email(message = "{email.invalid}")
        String email,

        @Pattern(message = "{phone.invalid}",
                regexp = ExceptionConstants.PHONE_PATTERN)
        @NotBlank(message = "{phone.blank}")
        String phone,
        Gender gender
) {
}

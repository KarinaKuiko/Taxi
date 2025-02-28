package com.example.registrationservice.controller;

import com.example.registrationservice.dto.create.SignInUserDto;
import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.TokenReadDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User management controller",
        description = "User management contains endpoints for signing in and signing up")
public interface UserManagementController {

    @Operation(summary = "Sign up",
            description = """
                    Required fields for signing up:\\s
                    - **username**: User's name\\s
                    - **firstName**: User's first name (non-empty string)\\s
                    - **lastName**: User's last name (non-empty string)\\s
                    - **email**: User's email address (non-empty string, valid email format)\\s
                    - **password**: User's password (non-empty string)\\s
                    - **phone**: User's phone number (non-empty string, must match the specified pattern)\\s
                    - **gender**: User's gender (MALE or FEMALE)\\s
                    - **role**: User's role (DRIVER or PASSENGER)\\s
                    - **carCreateEditDto**: Details of the car associated with the driver\\s
                    - **file**: User's avatar (optional)
                    Example:
                    {
                        "username": "driver27@gmail.com",
                        "firstName": "passenger",
                        "lastName": "passenger",
                        "email": "driver27@gmail.com",
                        "password": "password",
                        "phone": "+375441234567",
                        "gender": "MALE",
                        "role": "DRIVER",
                        "carCreateEditDto": {
                            "color": "red",
                            "brand": "BMW",
                            "number": "AB124CD",
                            "year": 2023
                        }
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was signed up successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    UserRepresentation signUp(@Valid @RequestPart SignUpDto dto,
                              @RequestPart(required = false) MultipartFile file);

    @Operation(summary = "Sign in",
            description = """
                    Required fields for signing in:\\s
                    - **email**: User's email address (non-empty string, valid email format)\\s
                    Example:
                    {
                        "email": "driver27@gmail.com",
                        "password": "password",
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was signed in successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "User with this email was not found"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    TokenReadDto signIn(@Valid @RequestBody SignInUserDto signInDto);
}

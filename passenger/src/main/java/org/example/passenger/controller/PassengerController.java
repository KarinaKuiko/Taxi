package org.example.passenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PageResponse;
import org.example.passenger.dto.read.PassengerReadDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Passenger controller", description = """
        Passenger controller contains endpoints for creating a new passenger, finding, updating and deleting passenger by id,\s
        retrieving list of passengers
        """)
public interface PassengerController {

    @Operation(summary = "Find all passengers",
            description = "Retrieves page of passengers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passengers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    PageResponse<PassengerReadDto> findAll(@RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

    @Operation(summary = "Find passenger by ID",
            description = "Retrieves data of passenger by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger's data was retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Passenger was not found")
    })
    PassengerReadDto findById(@PathVariable("id") Long id);

    @Operation(summary = "Create a new passenger",
            description = """
                    Required fields for creating a new passenger:\s
                    - **firstName**: Passenger's first name (non-empty string)\\s
                    - **lastName**: Passenger's last name (non-empty string)\\s
                    - **email**: Passenger's email address (non-empty string, valid email format)\\s
                    - **phone**: Passenger's phone number (non-empty string, must match the specified pattern)\\s
                    - **file**: Passenger's avatar (optional)
                    Example:
                    {
                        "firstName": "passenger",
                        "lastName": "passenger",
                        "email": "test1@gmail.com",
                        "phone": "+375441234567"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger was successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Passenger with this email already exists")
    })
    PassengerReadDto create(@RequestPart @Valid PassengerCreateEditDto dto,
                            @RequestPart(required = false) MultipartFile file);

    @Operation(summary = "Updating passenger by ID",
            description = """
                    Updating passenger's data by ID. Fields to update:\\s
                    - **firstName**: Passenger's first name (non-empty string)\\s
                    - **lastName**: Passenger's last name (non-empty string)\\s
                    - **email**: Passenger's email address (non-empty string, valid email format)\\s
                    - **phone**: Passenger's phone number (non-empty string, must match the specified pattern)\\s
                    - **file**: Passenger's avatar (optional)
                    Example:
                    {
                        "firstName": "passenger",
                        "lastName": "passenger",
                        "email": "test1@gmail.com",
                        "phone": "+375441234567"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "409", description = "Passenger with this email already exists")
    })
    PassengerReadDto update(@PathVariable("id") Long id,
                            @RequestPart @Valid PassengerCreateEditDto dto,
                            @RequestPart(required = false) MultipartFile file);

    @Operation(summary = "Soft deleting passenger by ID",
            description = "Mark passenger as deleted without removing it from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Passenger was deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Passenger was not found")
    })
    void delete(@PathVariable("id") Long id);
}

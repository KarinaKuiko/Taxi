package org.example.rating.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.rating.dto.create.RateCreateEditDto;
import org.example.rating.dto.read.PageResponse;
import org.example.rating.dto.read.RateReadDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Rate controller", description = """
        Rate controller contains endpoints for creating a new rate, finding, updating rate by id,\s
        retrieving list of rates
        """)
public interface RateController {

    @Operation(summary = "Find all driver's rates",
            description = "Retrieves page of driver's rates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver's rates retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    PageResponse<RateReadDto> findAllDriversRates(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

    @Operation(summary = "Find all passenger's rates",
            description = "Retrieves page of passenger's rates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger's rates retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    PageResponse<RateReadDto> findAllPassengersRates(@RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

    @Operation(summary = "Find driver's rate by ID",
            description = "Retrieves data of driver's rate by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver's rate data was retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Driver's rate was not found")
    })
    RateReadDto findDriverRateById(@PathVariable("id") Long id);

    @Operation(summary = "Find passenger's rate by ID",
            description = "Retrieves data of passenger's rate by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger's rate data was retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Passenger's rate was not found")
    })
    RateReadDto findPassengerRateById(@PathVariable("id") Long id);

    @Operation(summary = "Create a new rate",
            description = """
                    Required fields for creating a new rate:\s
                    - **rideId**: Ride's ID (non-empty string)\s
                    - **comment**: Comment \s
                    - **rating**: Ride's rate (non-empty string, valid range)\s
                    - **userId** : User's id (non-empty string)\s
                    - **userType** : User's type (DRIVER or PASSENGER)\s
                    Example:
                    {
                        "rideId": 1,
                        "comment": "comment",
                        "rating": 5,
                        "userId": 1,
                        "userType": "DRIVER"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car was successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    RateReadDto create(@RequestBody @Valid RateCreateEditDto dto);

    @Operation(summary = "Updating rate by ID",
            description = """
                    Updating rate's data by ID. Fields to update:\s
                    - **rideId**: Ride's ID (non-empty string)\s
                    - **comment**: Comment \s
                    - **rating**: Ride's rate (non-empty string, valid range)\s
                    - **userId** : User's id (non-empty string)\s
                    - **userType** : User's type (DRIVER or PASSENGER)\s
                    Example:
                    {
                        "rideId": 1,
                        "comment": "comment",
                        "rating": 5,
                        "userId": 1,
                        "userType": "DRIVER"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car's data was updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Car was not found")
    })
    RateReadDto update(@PathVariable("id") Long id, @RequestBody @Valid RateCreateEditDto dto);
}

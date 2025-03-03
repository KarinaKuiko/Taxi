package org.example.ride.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.ride.dto.create.DriverRideStatusDto;
import org.example.ride.dto.create.PassengerRideStatusDto;
import org.example.ride.dto.create.RideCreateEditDto;
import org.example.ride.dto.read.PageResponse;
import org.example.ride.dto.read.RideReadDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Ride controller", description = """
        Ride controller contains endpoints for creating a new ride, finding, updating ride by id,\s
        retrieving list of rides
        """)
public interface RideController {

    @Operation(summary = "Find all rides",
            description = "Retrieves page of rides. Also can find rides by passenger or driver id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rides retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Only one id-parameter can be provided at a time")
    })
    PageResponse<RideReadDto> findAll(@RequestParam(name = "driverId", required = false) Long driverId,
                                      @RequestParam(name = "passengerId", required = false) Long passengerId,
                                      @RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

    @Operation(summary = "Find ride by ID",
            description = "Retrieves data of ride by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride's data was retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ride was not found")
    })
    RideReadDto findById(@PathVariable("id") Long id);

    @Operation(summary = "Create a new ride",
            description = """
                    Required fields for creating a ride:\\s
                    - **driverId**: Driver's ID (optional, valid range)\\s
                    - **passengerId**: Passenger's ID (non-empty string, valid range)\\s
                    - **addressFrom**: Address from (non-empty string)\\s
                    - **addressTo**: Address to (non-empty string)\\s
                    Example:
                    {
                        "driverId": 1,
                        "passengerId": 1,
                        "addressFrom": "Minsk",
                        "addressTo": "Gomel"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride was successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    RideReadDto create(@RequestBody @Valid RideCreateEditDto dto);

    @Operation(summary = "Update ride by ID",
            description = """
                    Updating ride's data by ID. Fields to update:\\s
                    - **driverId**: Driver's ID (optional, valid range)\\s
                    - **passengerId**: Passenger's ID (non-empty string, valid range)\\s
                    - **addressFrom**: Address from (non-empty string)\\s
                    - **addressTo**: Address to (non-empty string)\\s
                    Example:
                    {
                        "driverId": 1,
                        "passengerId": 1,
                        "addressFrom": "Minsk",
                        "addressTo": "Gomel"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ride was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ride was not found")
    })
    RideReadDto update(@PathVariable("id") Long id, @RequestBody @Valid RideCreateEditDto dto);

    @Operation(summary = "Update driver's status by ID",
            description = """
                    Updating driver's status by ID. Fields to update:\\s
                    - **rideStatus**: Driver's status (CREATED, ACCEPTED, ON_WAY_FOR_PASSENGER, WAITING, \\s
                    ON_WAY_TO_DESTINATION, COMPLETED, CANCELED)\\s
                    Example:
                    {
                        "rideStatus": "ON_WAY_FOR_PASSENGER"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver's status was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ride was not found")
    })
    RideReadDto updateDriverStatus(@PathVariable("id") Long id, @RequestBody DriverRideStatusDto driverRideStatusDto);

    @Operation(summary = "Update passenger's status by ID",
            description = """
                    Updating passenger's status by ID. Fields to update:\\s
                    - **rideStatus**: Driver's status (WAITING, GETTING_OUT, IN_CAR)\\s
                    Example:
                    {
                        "rideStatus": "GETTING_OUT"
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Passenger's status was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Ride was not found")
    })
    RideReadDto updatePassengerStatus(@PathVariable("id") Long id, @RequestBody PassengerRideStatusDto passengerRideStatusDto);
}

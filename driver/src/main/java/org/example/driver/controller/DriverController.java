package org.example.driver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.PageResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Driver controller", description = """
        Driver controller contains endpoints for creating a new driver, finding, updating and deleting driver by id,\s
        retrieving list of drivers
        """)
public interface DriverController {

    @Operation(summary = "Find all drivers",
            description = "Retrieves page of drivers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    PageResponse<DriverReadDto> findAll(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

    @Operation(summary = "Find all drivers",
            description = "Retrieves list of drivers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drivers retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    List<DriverReadDto> findFullList();

    @Operation(summary = "Find driver by ID",
            description = "Retrieves data of driver by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver's data was retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Driver was not found")
    })
    DriverReadDto findById(@PathVariable("id") Long id);

    @Operation(summary = "Updating driver by ID",
            description = """
                    Updating driver's data by ID. Fields to update:\\s
                    - **firstName**: Driver's first name (non-empty string)\\s
                    - **lastName**: Driver's last name (non-empty string)\\s
                    - **email**: Driver's email address (non-empty string, valid email format)\\s
                    - **phone**: Driver's phone number (non-empty string, must match the specified pattern)\\s
                    - **gender**: Driver's gender (MALE or FEMALE)\\s
                    - **carCreateEditDto**: Details of the car associated with the driver\\s
                    - **file**: Driver's avatar (optional)
                    Example:
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@example.com",
                        "phone": "+1234567890",
                        "gender": "MALE",
                        "carCreateEditDto": {
                            "color": "red",
                            "brand": "Toyota",
                            "number": "AB125CK",
                            "year": 2020
                        }
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Driver was not found"),
            @ApiResponse(responseCode = "409", description = "Driver with this email already exists")
    })
    DriverReadDto update(@PathVariable("id") Long id,
                         @RequestPart @Valid DriverCreateEditDto dto,
                         @RequestPart(required = false) MultipartFile file);

    @Operation(summary = "Create a new driver",
            description = """
                    Required fields for creating a driver:\\s
                    - **firstName**: Driver's first name (non-empty string)\\s
                    - **lastName**: Driver's last name (non-empty string)\\s
                    - **email**: Driver's email address (non-empty string, valid email format)\\s
                    - **phone**: Driver's phone number (non-empty string, must match the specified pattern)\\s
                    - **gender**: Driver's gender (MALE or FEMALE)\\s
                    - **carCreateEditDto**: Details of the car associated with the driver\\s
                    - **file**: Driver's avatar (optional)
                    Example:
                    {
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@example.com",
                        "phone": "+1234567890",
                        "gender": "MALE",
                        "carCreateEditDto": {
                            "color": "red",
                            "brand": "Toyota",
                            "number": "XYZ1234",
                            "year": 2020
                        }
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Driver was successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Driver with this email already exists")
    })
    DriverReadDto create(@RequestPart @Valid DriverCreateEditDto dto,
                         @RequestPart(required = false) MultipartFile file);

    @Operation(summary = "Soft deleting driver by ID",
            description = "Mark driver as deleted without removing it from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Driver was deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Driver was not found")
    })
    void delete(@PathVariable("id") Long id);
}

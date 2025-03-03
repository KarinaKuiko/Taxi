package org.example.driver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.PageResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Car controller", description = """
        Car controller contains endpoints for creating a new car, finding, updating and deleting car by id,\s
        retrieving list of cars
        """)
public interface CarController {

    @Operation(summary = "Find all cars",
            description = "Retrieves page of cars")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cars retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    PageResponse<CarReadDto> findAll(@RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit);

    @Operation(summary = "Find car by ID",
            description = "Retrieves data of car by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car's data was retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Car was not found")
    })
    CarReadDto findById(@PathVariable("id") Long id);

    @Operation(summary = "Create a new car",
            description = """
                    Required fields for creating a new car:\s
                    - **color**: Color of the car (non-empty string)\s
                    - **brand**: Brand of the car (non-empty string)\s
                    - **number**: Unique number of the car (non-empty string, valid format)\s
                    - **year** : Car's year of manufacture (non-empty string, valid range)
                    Example:
                    {
                        "color": "blue",
                        "brand": "BMW",
                        "number": "AB125CK",
                        "year": 2023
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car was successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Car with this number already exists")
    })
    CarReadDto create(@RequestBody @Valid CarCreateEditDto dto);

    @Operation(summary = "Updating car by ID",
            description = """
                    Updating car's data by ID. Fields to update:\s
                    - **color**: Color of the car (non-empty string)\s
                    - **brand**: Brand of the car (non-empty string)\s
                    - **number**: Unique number of the car (non-empty string, valid format)\s
                    - **year** : Car's year of manufacture (non-empty string, valid range)
                    Example:
                    {
                        "color": "blue",
                        "brand": "BMW",
                        "number": "AB125CK",
                        "year": 2023
                    }
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Car's data was updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Car was not found"),
            @ApiResponse(responseCode = "409", description = "Car with this number already exists")
    })
    CarReadDto update(@PathVariable("id") Long id,
                      @RequestBody @Valid CarCreateEditDto dto);

    @Operation(summary = "Soft deleting car by ID",
            description = "Mark car as deleted without removing it from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Car was deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Car was not found")
    })
    void delete(@PathVariable("id") Long id);
}

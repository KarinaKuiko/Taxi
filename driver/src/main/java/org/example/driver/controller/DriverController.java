package org.example.driver.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.PageResponse;
import org.example.driver.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
@Validated
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public PageResponse<DriverReadDto> findAll(@RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return PageResponse.of(driverService.findAll(page, limit));
    }

    @GetMapping("/{id}")
    public DriverReadDto findById(@PathVariable("id") Long id) {
        return driverService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverReadDto create(@RequestBody @Valid DriverCreateEditDto dto) {
        return driverService.create(dto);
    }

    @PutMapping("/{id}")
    public DriverReadDto update(@PathVariable("id") Long id,
                                @RequestBody @Valid DriverCreateEditDto dto) {
        return driverService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        driverService.safeDelete(id);
    }
}

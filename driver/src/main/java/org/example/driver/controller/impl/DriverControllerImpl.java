package org.example.driver.controller.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.driver.controller.DriverController;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.dto.read.PageResponse;
import org.example.driver.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/drivers")
@Validated
public class DriverControllerImpl implements DriverController {
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DriverReadDto create(@RequestPart @Valid DriverCreateEditDto dto,
                                @RequestPart(required = false) MultipartFile file) {
        return driverService.create(dto, file);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DriverReadDto update(@PathVariable("id") Long id,
                                @RequestPart @Valid DriverCreateEditDto dto,
                                @RequestPart(required = false) MultipartFile file) {
        return driverService.update(id, dto, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        driverService.safeDelete(id);
    }
}

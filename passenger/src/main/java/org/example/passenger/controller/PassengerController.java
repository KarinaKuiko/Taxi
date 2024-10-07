package org.example.passenger.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PageResponse;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.service.PassengerService;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
public class PassengerController {
    public final PassengerService passengerService;

    @GetMapping
    public PageResponse<PassengerReadDto> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                  @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer limit) {
        return PageResponse.of(passengerService.findAll(page, limit));
    }

    @GetMapping("/{id}")
    public PassengerReadDto findById(@PathVariable("id") Long id) {
        return passengerService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerReadDto create(@RequestBody @Valid PassengerCreateEditDto dto) {
        return passengerService.create(dto);
    }

    @PutMapping("/{id}")
    public PassengerReadDto update(@PathVariable("id") Long id,
                                   @RequestBody @Valid PassengerCreateEditDto dto) {
        return passengerService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        passengerService.safeDelete(id);
    }
}

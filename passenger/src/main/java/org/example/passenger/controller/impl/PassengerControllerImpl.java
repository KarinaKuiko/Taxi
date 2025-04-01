package org.example.passenger.controller.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.passenger.annotation.ValidateAccess;
import org.example.passenger.controller.PassengerController;
import org.example.passenger.dto.create.PassengerCreateEditDto;
import org.example.passenger.dto.read.PageResponse;
import org.example.passenger.dto.read.PassengerReadDto;
import org.example.passenger.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
@RequestMapping("/api/v1/passengers")
@RequiredArgsConstructor
@Validated
public class PassengerControllerImpl implements PassengerController {
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerReadDto create(@RequestPart @Valid PassengerCreateEditDto dto,
                                   @RequestPart(required = false) MultipartFile file) {
        return passengerService.create(dto, file);
    }

    @PutMapping(value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ValidateAccess
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public PassengerReadDto update(@PathVariable("id") Long id,
                                   @RequestPart @Valid PassengerCreateEditDto dto,
                                   @RequestPart(required = false) MultipartFile file,
                                   JwtAuthenticationToken token) {
        return passengerService.update(id, dto, file);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    @ValidateAccess
    public void delete(@PathVariable("id") Long id,
                       JwtAuthenticationToken token) {
        passengerService.safeDelete(id);
    }
}

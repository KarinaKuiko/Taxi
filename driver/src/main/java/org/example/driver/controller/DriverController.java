package org.example.driver.controller;

import lombok.RequiredArgsConstructor;
import org.example.driver.dto.create.DriverCreateEditDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverReadDto>> findAll() {
        return ok().body(driverService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverReadDto> findById(@PathVariable("id") Long id) {
        return driverService.findById(id)
                .map(obj -> ok()
                        .body(obj))
                .orElseGet(notFound()::build);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DriverReadDto> create(@RequestBody DriverCreateEditDto dto) {
        return ok().body(driverService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverReadDto> update(@PathVariable("id") Long id,
                                             @RequestBody DriverCreateEditDto dto) {
        return driverService.update(id, dto)
                .map(obj -> ok().body(obj))
                .orElseGet(notFound()::build);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return driverService.safeDelete(id)
                ? noContent().build()
                : notFound().build();
    }
}

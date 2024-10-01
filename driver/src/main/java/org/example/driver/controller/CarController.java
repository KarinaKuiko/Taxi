package org.example.driver.controller;

import lombok.RequiredArgsConstructor;
import org.example.driver.dto.create.CarCreateEditDto;
import org.example.driver.dto.read.CarReadDto;
import org.example.driver.dto.read.DriverReadDto;
import org.example.driver.service.CarService;
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
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<CarReadDto>> findAll() {
        return ok().body(carService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarReadDto> findById(@PathVariable("id") Long id) {
        return carService.findById(id)
                .map(obj -> ok()
                        .body(obj))
                .orElseGet(notFound()::build);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CarReadDto> create(@RequestBody CarCreateEditDto dto) {
        return ok().body(carService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarReadDto> update(@PathVariable("id")Long id,
                                             @RequestBody CarCreateEditDto dto) {
        return carService.update(id, dto)
                .map(obj -> ok().body(obj))
                .orElseGet(notFound()::build);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return carService.safeDelete(id)
                ? noContent().build()
                : notFound().build();
    }
}

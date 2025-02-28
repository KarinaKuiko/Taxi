package com.example.registrationservice.controller.impl;

import com.example.registrationservice.dto.create.SignInUserDto;
import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.TokenReadDto;
import com.example.registrationservice.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/taxi")
@RequiredArgsConstructor
@Slf4j
public class UserManagementControllerImpl {

    private final UserManagementService userManagementService;

    @PostMapping(value = "/sign-up",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserRepresentation signUp(@Valid @RequestPart SignUpDto dto,
                                     @RequestPart(required = false) MultipartFile file) {
        return userManagementService.signUp(dto, file);
    }

    @PostMapping("/sign-in")
    public TokenReadDto signIn(@Valid @RequestBody SignInUserDto signInDto) {
        return userManagementService.signIn(signInDto);
    }
}

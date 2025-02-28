package org.example.passenger.controller.impl;

import lombok.RequiredArgsConstructor;
import org.example.passenger.constants.MinioConstants;
import org.example.passenger.controller.AvatarController;
import org.example.passenger.dto.read.MinioAvatar;
import org.example.passenger.service.AvatarService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/passengers/avatars")
public class AvatarControllerImpl implements AvatarController {

    private final AvatarService avatarService;

    @GetMapping("/{name}")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable("name") String name) {
        MinioAvatar avatar = avatarService.getAvatar(name);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        MinioConstants.CONTENT_DISPOSITION_VALUE)
                .contentType(avatar.mediaType())
                .body(new InputStreamResource(avatar.inputStream()));
    }
}

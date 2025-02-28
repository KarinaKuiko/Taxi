package org.example.passenger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Avatar controller",
        description = "Avatar controller contains endpoint for getting avatar by name")
public interface AvatarController {

    @Operation(summary = "Getting avatar by name",
            description = "Retrieves avatar by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avatar was retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Avatar was not found")
    })
    ResponseEntity<InputStreamResource> getAvatar(@PathVariable("name") String name);
}

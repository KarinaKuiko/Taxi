package com.example.registrationservice.service;

import com.example.registrationservice.client.DriverClient;
import com.example.registrationservice.client.PassengerClient;
import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.ExceptionDto;
import com.example.registrationservice.exception.KeycloakException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.registrationservice.constants.AppConstants.BEARER_PREFIX;
import static com.example.registrationservice.constants.ServiceConstants.DRIVER_ROLE;
import static com.example.registrationservice.constants.ServiceConstants.GENDER_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.PASSENGER_ROLE;
import static com.example.registrationservice.constants.ServiceConstants.PHONE_FIELD;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final Keycloak keycloak;

    private final DriverClient driverClient;
    private final PassengerClient passengerClient;
    private final ObjectMapper objectMapper;

    @Value("${keycloak.realm}")
    private String realm;

    public UserRepresentation signUp(SignUpDto dto, MultipartFile file) {
        UserRepresentation keycloakUser = getUserRepresentation(dto);
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(keycloakUser);

        if (response.getStatus() == HttpStatus.CREATED.value()) {
            String adminClientAccessToken = keycloak.tokenManager().getAccessTokenString();
            try {
                if (Objects.equals(dto.role().name(), PASSENGER_ROLE)) {
                    passengerClient.createPassenger(dto, file,
                            BEARER_PREFIX + adminClientAccessToken);
                } else if (Objects.equals(dto.role().name(), DRIVER_ROLE)){
                    driverClient.createDriver(dto, file,
                            BEARER_PREFIX + adminClientAccessToken);
                }
            } catch (Exception exception) {
                usersResource.delete(CreatedResponseUtil.getCreatedId(response));
                throw exception;
            }
        } else {
            try {
                throw new KeycloakException(objectMapper.readValue(readResponseBody(response), ExceptionDto.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        RolesResource rolesResource = realmResource.roles();
        RoleRepresentation role = rolesResource.get(dto.role().name()).toRepresentation();
        UserResource userById = usersResource.get(CreatedResponseUtil.getCreatedId(response));
        userById.roles()
                .realmLevel()
                .add(List.of(role));

        return userById.toRepresentation();
    }

    private UserRepresentation getUserRepresentation(SignUpDto signUpDto) {
        UserRepresentation user = new UserRepresentation();
        CredentialRepresentation credential = new CredentialRepresentation();

        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(signUpDto.password());
        credential.setTemporary(false);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put(GENDER_FIELD, List.of(signUpDto.gender().name()));
        attributes.put(PHONE_FIELD, List.of(signUpDto.phone()));

        user.setUsername(signUpDto.username());
        user.setFirstName(signUpDto.firstName());
        user.setLastName(signUpDto.lastName());
        user.setEmail(signUpDto.email());
        user.setCredentials(List.of(credential));
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setAttributes(attributes);

        return user;
    }

    private String readResponseBody(Response response) {
        if (response.hasEntity()) {
            return response.readEntity(String.class);
        }
        return "";
    }
}

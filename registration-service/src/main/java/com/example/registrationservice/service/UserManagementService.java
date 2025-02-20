package com.example.registrationservice.service;

import com.example.exceptionhandlerstarter.dto.ExceptionDto;
import com.example.exceptionhandlerstarter.keycloak.KeycloakException;
import com.example.registrationservice.client.DriverClient;
import com.example.registrationservice.client.PassengerClient;
import com.example.registrationservice.dto.create.SignInUserDto;
import com.example.registrationservice.dto.create.SignUpDto;
import com.example.registrationservice.dto.read.TokenReadDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.registrationservice.constants.AppConstants.BEARER_PREFIX;
import static com.example.registrationservice.constants.ServiceConstants.CLIENT_ID_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.CLIENT_SECRET_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.DRIVER_ROLE;
import static com.example.registrationservice.constants.ServiceConstants.GENDER_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.GRANT_TYPE_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.GRANT_TYPE_PASSWORD_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.PASSENGER_ROLE;
import static com.example.registrationservice.constants.ServiceConstants.PASSWORD_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.PHONE_FIELD;
import static com.example.registrationservice.constants.ServiceConstants.USERNAME_FIELD;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final Keycloak keycloak;

    private final DriverClient driverClient;
    private final PassengerClient passengerClient;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-client-id}")
    private String authClientId;

    @Value("${keycloak.auth-client-secret}")
    private String authClientSecret;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    public UserRepresentation signUp(SignUpDto signUpDto) {
        UserRepresentation keycloakUser = getUserRepresentation(signUpDto);
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(keycloakUser);

        if (response.getStatus() == HttpStatus.CREATED.value()) {
            String adminClientAccessToken = keycloak.tokenManager().getAccessTokenString();
            try {
                if (Objects.equals(signUpDto.role().name(), PASSENGER_ROLE)) {
                    passengerClient.createPassenger(signUpDto,
                            BEARER_PREFIX + adminClientAccessToken);
                } else if (Objects.equals(signUpDto.role().name(), DRIVER_ROLE)) {
                    driverClient.createDriver(signUpDto,
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
        RoleRepresentation role = rolesResource.get(signUpDto.role().name()).toRepresentation();
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

    @SneakyThrows
    public TokenReadDto signIn(@Valid SignInUserDto signInDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(GRANT_TYPE_FIELD, GRANT_TYPE_PASSWORD_FIELD);
        body.add(USERNAME_FIELD, signInDto.email());
        body.add(PASSWORD_FIELD, signInDto.password());
        body.add(CLIENT_ID_FIELD, authClientId);
        body.add(CLIENT_SECRET_FIELD, authClientSecret);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = getToken(requestEntity);


        return objectMapper.readValue(response.getBody(),
                TokenReadDto.class);
    }

    private ResponseEntity<String> getToken(HttpEntity<MultiValueMap<String, String>> requestEntity) {
        String authUrl = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(authUrl, HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            throw new KeycloakException(
                    new ExceptionDto(HttpStatus.valueOf(e.getStatusCode().value()),
                            e.getMessage(),
                            LocalDateTime.now()));
        }
        return response;
    }
}
package com.example.registrationservice.client;


import com.example.registrationservice.dto.read.ExceptionDto;
import com.example.registrationservice.exception.ClientException;
import com.example.registrationservice.exception.keycloak.UnauthorizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.example.registrationservice.constants.ExceptionConstants.UNAUTHORIZED_MESSAGE;

@Component
@RequiredArgsConstructor
public class ClientErrorDecoder implements ErrorDecoder {

    private final MessageSource messageSource;
    private final Default eDefault = new Default();

    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        if (response.status() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return eDefault.decode(s, response);
        }

        if (response.status() == HttpStatus.UNAUTHORIZED.value()) {
            return new UnauthorizedException(messageSource.getMessage(
                    UNAUTHORIZED_MESSAGE,
                    new Object[]{},
                    LocaleContextHolder.getLocale()));
        }

        try {
            return new ClientException(objectMapper.readValue(readResponseBody(response), ExceptionDto.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private String readResponseBody(Response response) {
        if (Objects.nonNull(response.body())) {
            @Cleanup InputStreamReader inputStreamReader = new InputStreamReader(response.body()
                    .asInputStream(), StandardCharsets.UTF_8);
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
        return "";
    }
}

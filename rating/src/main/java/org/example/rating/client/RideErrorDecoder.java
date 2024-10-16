package org.example.rating.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import org.example.rating.dto.read.ExceptionDto;
import org.example.rating.exception.ride.RideException;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class RideErrorDecoder implements ErrorDecoder {
    @Override
    @SneakyThrows
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        ExceptionDto exceptionDto;
        exceptionDto = objectMapper.readValue(readResponseBody(response), ExceptionDto.class);
        return new RideException(exceptionDto);
    }

    @SneakyThrows
    private String readResponseBody(Response response) {
        if (Objects.nonNull(response.body())) {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().asInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        }
        return "";
    }
}

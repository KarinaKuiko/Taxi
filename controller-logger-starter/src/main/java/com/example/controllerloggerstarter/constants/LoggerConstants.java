package com.example.controllerloggerstarter.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoggerConstants {
    public static final String REQUEST_MESSAGE = "Request: method: {}, URI: {}, Headers: {}, Body: {}";
    public static final String RESPONSE_MESSAGE = "Response: method: {}, URI: {}, Body: {}";
}

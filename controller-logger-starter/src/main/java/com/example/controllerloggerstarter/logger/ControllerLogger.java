package com.example.controllerloggerstarter.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Objects;

import static com.example.controllerloggerstarter.constants.LoggerConstants.REQUEST_MESSAGE;
import static com.example.controllerloggerstarter.constants.LoggerConstants.RESPONSE_MESSAGE;

@RequiredArgsConstructor
@Configuration
@Aspect
@Slf4j
public class ControllerLogger {

    private final ObjectMapper objectMapper;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String requestBody = getRequestBody(joinPoint);

        log.info(REQUEST_MESSAGE,
                request.getMethod(),
                request.getRequestURI(),
                getHeaders(request),
                requestBody);

        return getResponse(joinPoint, request);
    }

    private Object getResponse(ProceedingJoinPoint joinPoint, HttpServletRequest request) throws Throwable {
        Object result = joinPoint.proceed();

        log.info(RESPONSE_MESSAGE,
                request.getMethod(),
                request.getRequestURI(),
                objectMapper.writeValueAsString(result));

        return result;
    }

    private String getRequestBody(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        int argLength = args.length;
        for (int i = 0; i < argLength; i++) {
            if (method.getParameterAnnotations()[i] != null) {
                for (Annotation annotation : method.getParameterAnnotations()[i]) {
                    if (annotation instanceof RequestBody) {
                        try {
                            return objectMapper.writeValueAsString(args[i]);
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            }
        }
        return "";
    }

    private String getHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName)
                .append(": ")
                .append(headerValue)
                .append(System.lineSeparator());
        }
        return headers.toString();
    }

}

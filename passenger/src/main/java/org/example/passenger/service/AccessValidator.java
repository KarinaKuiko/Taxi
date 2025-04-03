package org.example.passenger.service;

import com.example.exceptionhandlerstarter.exception.security.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.passenger.constants.ExceptionConstants;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.example.passenger.constants.SecurityConstants.EMAIL_CLAIM;
import static org.example.passenger.constants.SecurityConstants.PREFIX_ROLE;
import static org.example.passenger.constants.SecurityConstants.ROLE_ADMIN;

@Aspect
@Component
@RequiredArgsConstructor
public class AccessValidator {

    private final PassengerService passengerService;
    private final MessageSource messageSource;

    @Around("@annotation(org.example.passenger.annotation.ValidateAccess)")
    public Object validateAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) args[args.length - 1];
        Long driverId = (Long) args[0];
        if (authenticationToken.getAuthorities()
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(authority -> authority.getAuthority()
                        .equals(PREFIX_ROLE + ROLE_ADMIN))) {
            return joinPoint.proceed(args);
        }
        if (!Objects.equals(passengerService.findById(driverId).email(),
                authenticationToken.getToken().getClaims().get(EMAIL_CLAIM))) {
            throw new AccessDeniedException(messageSource.getMessage(
                    ExceptionConstants.ACCESS_DENIED_EXCEPTION,
                    new Object[]{},
                    LocaleContextHolder.getLocale()));
        }
        return joinPoint.proceed(args);
    }
}

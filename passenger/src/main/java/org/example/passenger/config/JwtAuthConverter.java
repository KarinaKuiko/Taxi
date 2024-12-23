package org.example.passenger.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.passenger.constants.SecurityConstants.PREFIX_ROLE;
import static org.example.passenger.constants.SecurityConstants.REALM_ACCESS;
import static org.example.passenger.constants.SecurityConstants.RESOURCE_ACCESS;
import static org.example.passenger.constants.SecurityConstants.ROLES;
import static org.example.passenger.constants.SecurityConstants.TAXI_CLIENT;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<? extends GrantedAuthority> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();

        Map<String, Object> realmAccess = jwt.getClaim(REALM_ACCESS);
        if (realmAccess != null && realmAccess.containsKey(ROLES)) {
            roles.addAll((Collection<? extends String>) realmAccess.get(ROLES));
        }

        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess != null && resourceAccess.containsKey(TAXI_CLIENT)) {
            Map<String, Object> demoAccess = (Map<String, Object>) resourceAccess.get(TAXI_CLIENT);
            if (demoAccess != null && demoAccess.containsKey(ROLES)) {
                roles.addAll((Collection<? extends String>) demoAccess.get(ROLES));
            }
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(PREFIX_ROLE + role.toUpperCase()))
                .collect(Collectors.toSet());
    }
}
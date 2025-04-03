package org.example.driver.config;

import org.example.driver.constants.SecurityConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.driver.constants.SecurityConstants.AZP_CLAIM;
import static org.example.driver.constants.SecurityConstants.AZP_CLAIM_VALUE;
import static org.example.driver.constants.SecurityConstants.PREFIX_ROLE;
import static org.example.driver.constants.SecurityConstants.REALM_ACCESS;
import static org.example.driver.constants.SecurityConstants.ROLES;
import static org.example.driver.constants.SecurityConstants.ROLE_ADMIN;

public class KeycloakJwtTokenConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        if (jwt.getClaim(AZP_CLAIM).equals(AZP_CLAIM_VALUE)) {
            return Collections.singletonList(new SimpleGrantedAuthority(PREFIX_ROLE + ROLE_ADMIN));
        }
        Map<String, Object> realmAccessMap = jwt.getClaimAsMap(REALM_ACCESS);
        Object roles = realmAccessMap.get(ROLES);

        if (roles instanceof List) {
            List<String> realmAccessList = ((List<?>) roles)
                    .stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();

            return realmAccessList.stream()
                    .map(role -> PREFIX_ROLE + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}

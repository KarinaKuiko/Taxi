package org.example.passenger.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {

    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
    public static final String RESOURCE_ACCESS = "resource_access";
    public static final String TAXI_CLIENT = "taxi_client";
    public static final String PREFIX_ROLE = "ROLE_";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String EMAIL_CLAIM = "email";
    public static final String AZP_CLAIM = "azp";
    public static final String AZP_CLAIM_VALUE = "admin-cli";
}

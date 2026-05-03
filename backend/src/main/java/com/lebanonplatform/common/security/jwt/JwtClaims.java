package com.lebanonplatform.common.security.jwt;

import java.util.Set;
import java.util.UUID;

public record JwtClaims(
        UUID userId,
        Set<String> roles
) {
}

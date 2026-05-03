package com.lebanonplatform.common.security;

import java.util.Set;
import java.util.UUID;

public record CurrentUser(
        UUID id,
        String phoneNumber,
        Set<String> roles
) {
}

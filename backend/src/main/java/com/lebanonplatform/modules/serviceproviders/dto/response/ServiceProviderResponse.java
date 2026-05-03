package com.lebanonplatform.modules.serviceproviders.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ServiceProviderResponse(
        UUID id,
        UUID ownerUserId,
        String name,
        String phoneNumber,
        String address,
        String city,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

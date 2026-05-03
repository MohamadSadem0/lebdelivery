package com.lebanonplatform.modules.clients.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ClientAddressResponse(
        UUID id,
        String label,
        String fullAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String phoneNumber,
        String instructions,
        boolean defaultAddress,
        Instant createdAt,
        Instant updatedAt
) {
}

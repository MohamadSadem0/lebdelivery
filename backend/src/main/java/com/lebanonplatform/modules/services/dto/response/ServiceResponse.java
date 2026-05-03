package com.lebanonplatform.modules.services.dto.response;

import com.lebanonplatform.modules.services.domain.PricingType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceResponse(
        UUID id,
        UUID serviceProviderId,
        String serviceProviderName,
        String name,
        String description,
        BigDecimal basePrice,
        PricingType pricingType,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

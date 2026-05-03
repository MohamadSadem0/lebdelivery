package com.lebanonplatform.modules.services.dto.request;

import com.lebanonplatform.modules.services.domain.PricingType;
import java.math.BigDecimal;

public record UpdateServiceRequest(
        String name,
        String description,
        BigDecimal basePrice,
        PricingType pricingType,
        Boolean active
) {
}

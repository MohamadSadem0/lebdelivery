package com.lebanonplatform.modules.services.dto.request;

import com.lebanonplatform.modules.services.domain.PricingType;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateServiceRequest(
        @NotBlank String name,
        String description,
        BigDecimal basePrice,
        PricingType pricingType,
        Boolean active
) {
}

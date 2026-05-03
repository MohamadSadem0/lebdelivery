package com.lebanonplatform.modules.products.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.Map;

public record ProductVariantRequest(
        @NotBlank String name,
        String sku,
        BigDecimal priceAdjustment,
        Integer stockQuantity,
        Map<String, String> attributes,
        @com.fasterxml.jackson.annotation.JsonProperty("isAvailable") Boolean available
) {
}

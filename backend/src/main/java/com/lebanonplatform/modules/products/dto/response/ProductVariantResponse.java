package com.lebanonplatform.modules.products.dto.response;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record ProductVariantResponse(
        UUID id,
        String name,
        String sku,
        BigDecimal priceAdjustment,
        int stockQuantity,
        Map<String, String> attributes,
        @com.fasterxml.jackson.annotation.JsonProperty("isAvailable") boolean available
) {
}

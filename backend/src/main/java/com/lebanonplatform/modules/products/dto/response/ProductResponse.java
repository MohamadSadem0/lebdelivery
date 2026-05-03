package com.lebanonplatform.modules.products.dto.response;

import com.lebanonplatform.modules.products.domain.StockStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID storeId,
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        @com.fasterxml.jackson.annotation.JsonProperty("isAvailable") boolean available,
        StockStatus stockStatus,
        List<ProductAttributeResponse> attributes,
        List<ProductVariantResponse> variants,
        Instant createdAt,
        Instant updatedAt
) {
}

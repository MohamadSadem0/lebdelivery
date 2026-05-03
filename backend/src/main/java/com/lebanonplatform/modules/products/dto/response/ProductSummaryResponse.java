package com.lebanonplatform.modules.products.dto.response;

import com.lebanonplatform.modules.products.domain.StockStatus;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID id,
        UUID storeId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        @com.fasterxml.jackson.annotation.JsonProperty("isAvailable") boolean available,
        StockStatus stockStatus
) {
}

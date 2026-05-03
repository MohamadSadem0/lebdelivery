package com.lebanonplatform.modules.products.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record UpdateProductRequest(
        UUID categoryId,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        @com.fasterxml.jackson.annotation.JsonProperty("isAvailable") Boolean available,
        List<ProductAttributeRequest> attributes
) {
}

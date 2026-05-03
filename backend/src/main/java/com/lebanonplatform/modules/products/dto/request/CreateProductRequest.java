package com.lebanonplatform.modules.products.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateProductRequest(
        UUID categoryId,
        @NotBlank String name,
        String description,
        @NotNull BigDecimal price,
        String imageUrl,
        @com.fasterxml.jackson.annotation.JsonProperty("isAvailable") Boolean available,
        @Valid List<ProductAttributeRequest> attributes,
        @Valid List<ProductVariantRequest> variants,
        @Valid InitialInventoryRequest initialInventory
) {
}

package com.lebanonplatform.modules.inventory.dto.request;

import java.util.UUID;

public record InventoryAdjustmentRequest(
        UUID productVariantId,
        int quantityChange,
        String reason
) {
}

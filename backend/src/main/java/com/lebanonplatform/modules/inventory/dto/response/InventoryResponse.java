package com.lebanonplatform.modules.inventory.dto.response;

import com.lebanonplatform.modules.inventory.domain.InventoryMode;
import com.lebanonplatform.modules.inventory.domain.UnitType;
import java.time.Instant;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID storeId,
        UUID productId,
        UUID productVariantId,
        InventoryMode inventoryMode,
        int quantity,
        int reservedQuantity,
        int lowStockThreshold,
        UnitType unitType,
        Instant updatedAt
) {
}

package com.lebanonplatform.modules.inventory.dto.response;

import com.lebanonplatform.modules.inventory.domain.InventoryMovementType;
import java.time.Instant;
import java.util.UUID;

public record InventoryMovementResponse(
        UUID id,
        UUID storeId,
        UUID productId,
        UUID productVariantId,
        InventoryMovementType movementType,
        int quantityChange,
        int previousQuantity,
        int newQuantity,
        String reason,
        String referenceType,
        UUID referenceId,
        UUID createdByUserId,
        Instant createdAt
) {
}

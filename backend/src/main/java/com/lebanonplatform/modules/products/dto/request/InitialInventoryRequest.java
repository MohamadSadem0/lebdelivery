package com.lebanonplatform.modules.products.dto.request;

import com.lebanonplatform.modules.inventory.domain.UnitType;
import jakarta.validation.constraints.PositiveOrZero;

public record InitialInventoryRequest(
        @PositiveOrZero int quantity,
        @PositiveOrZero int lowStockThreshold,
        UnitType unitType
) {
}

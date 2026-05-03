package com.lebanonplatform.modules.stores.dto.response;

import com.lebanonplatform.modules.inventory.domain.InventoryMode;
import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import java.util.List;
import java.util.UUID;

public record ProductFormConfigResponse(
        UUID storeId,
        StoreTypeCode storeTypeCode,
        InventoryMode inventoryMode,
        boolean supportsVariants,
        boolean supportsExpiryDate,
        boolean supportsWeightItems,
        boolean supportsModifiers,
        boolean supportsPreparationTime,
        boolean supportsPrescriptionFlag,
        List<String> enabledFeatures,
        List<String> requiredProductFields,
        List<String> optionalProductFields
) {
}

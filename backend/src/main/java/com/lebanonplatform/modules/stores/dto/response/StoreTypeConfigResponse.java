package com.lebanonplatform.modules.stores.dto.response;

import com.lebanonplatform.modules.inventory.domain.InventoryMode;
import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import java.util.List;

public record StoreTypeConfigResponse(
        StoreTypeCode storeTypeCode,
        String displayName,
        InventoryMode inventoryMode,
        List<String> enabledFeatures,
        List<String> requiredProductFields,
        List<String> optionalProductFields,
        boolean supportsModifiers,
        boolean supportsVariants,
        boolean supportsExpiryDate,
        boolean supportsWeightItems,
        boolean supportsPreparationTime,
        boolean supportsPrescriptionFlag
) {
}

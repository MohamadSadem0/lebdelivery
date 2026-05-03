package com.lebanonplatform.modules.stores.dto.response;

import com.lebanonplatform.modules.stores.domain.DeliveryMode;
import com.lebanonplatform.modules.stores.domain.StoreStatus;
import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import java.math.BigDecimal;
import java.util.UUID;

public record StoreSummaryResponse(
        UUID id,
        String name,
        String description,
        StoreTypeCode storeTypeCode,
        String phone,
        String address,
        DeliveryMode deliveryMode,
        StoreStatus status,
        boolean openNow,
        BigDecimal ratingAverage,
        int ratingCount
) {
}

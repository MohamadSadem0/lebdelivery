package com.lebanonplatform.modules.stores.dto.response;

import com.lebanonplatform.modules.stores.domain.DeliveryMode;
import com.lebanonplatform.modules.stores.domain.StoreStatus;
import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record StoreResponse(
        UUID id,
        UUID ownerUserId,
        String name,
        String description,
        StoreTypeCode storeTypeCode,
        String phone,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        DeliveryMode deliveryMode,
        StoreStatus status,
        boolean openNow,
        Map<String, Object> openingHours,
        BigDecimal minimumOrderAmount,
        Integer averagePreparationMinutes,
        BigDecimal ratingAverage,
        int ratingCount,
        Instant createdAt,
        Instant updatedAt
) {
}

package com.lebanonplatform.modules.stores.dto.request;

import com.lebanonplatform.modules.stores.domain.DeliveryMode;
import java.math.BigDecimal;
import java.util.Map;

public record UpdateStoreRequest(
        String name,
        String description,
        String phone,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        DeliveryMode deliveryMode,
        Map<String, Object> openingHours,
        BigDecimal minimumOrderAmount,
        Integer averagePreparationMinutes
) {
}

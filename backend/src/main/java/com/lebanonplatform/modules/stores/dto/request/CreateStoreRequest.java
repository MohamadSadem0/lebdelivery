package com.lebanonplatform.modules.stores.dto.request;

import com.lebanonplatform.modules.stores.domain.DeliveryMode;
import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

public record CreateStoreRequest(
        @NotBlank String name,
        String description,
        @NotNull StoreTypeCode storeTypeCode,
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

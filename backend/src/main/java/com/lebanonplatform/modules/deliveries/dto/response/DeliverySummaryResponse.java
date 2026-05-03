package com.lebanonplatform.modules.deliveries.dto.response;

import com.lebanonplatform.modules.deliveries.domain.DeliveryStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DeliverySummaryResponse(
        UUID id,
        UUID orderId,
        String orderNumber,
        String storeName,
        UUID driverId,
        DeliveryStatus status,
        BigDecimal deliveryFee,
        String pickupAddressSnapshot,
        String dropoffAddressSnapshot,
        Instant createdAt,
        Instant updatedAt
) {
}

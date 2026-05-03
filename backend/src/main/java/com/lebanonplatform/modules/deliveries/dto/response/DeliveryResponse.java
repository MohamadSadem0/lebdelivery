package com.lebanonplatform.modules.deliveries.dto.response;

import com.lebanonplatform.modules.deliveries.domain.DeliveryStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DeliveryResponse(
        UUID id,
        UUID orderId,
        String orderNumber,
        UUID storeId,
        String storeName,
        UUID driverId,
        String driverName,
        DeliveryStatus status,
        String pickupAddressSnapshot,
        String dropoffAddressSnapshot,
        BigDecimal deliveryFee,
        Instant acceptedAt,
        Instant pickedUpAt,
        Instant deliveredAt,
        String failureReason,
        String cancellationReason,
        Instant createdAt,
        Instant updatedAt
) {
}

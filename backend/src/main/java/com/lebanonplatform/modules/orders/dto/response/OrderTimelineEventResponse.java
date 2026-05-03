package com.lebanonplatform.modules.orders.dto.response;

import com.lebanonplatform.modules.orders.domain.OrderStatus;
import java.time.Instant;
import java.util.UUID;

public record OrderTimelineEventResponse(
        UUID id,
        OrderStatus status,
        String title,
        String description,
        UUID createdByUserId,
        Instant createdAt
) {
}

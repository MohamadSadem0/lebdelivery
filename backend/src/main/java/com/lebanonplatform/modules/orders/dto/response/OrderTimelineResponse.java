package com.lebanonplatform.modules.orders.dto.response;

import java.util.List;
import java.util.UUID;

public record OrderTimelineResponse(
        UUID orderId,
        List<OrderTimelineEventResponse> events
) {
}

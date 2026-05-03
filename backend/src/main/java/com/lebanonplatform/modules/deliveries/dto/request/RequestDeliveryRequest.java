package com.lebanonplatform.modules.deliveries.dto.request;

import java.math.BigDecimal;

public record RequestDeliveryRequest(
        String pickupAddress,
        String dropoffAddress,
        BigDecimal deliveryFee
) {
}

package com.lebanonplatform.modules.orders.dto.response;

import com.lebanonplatform.modules.orders.domain.FulfillmentType;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderSummaryResponse(
        UUID id,
        String orderNumber,
        UUID storeId,
        String storeName,
        OrderStatus status,
        PaymentStatus paymentStatus,
        FulfillmentType fulfillmentType,
        Instant scheduledFor,
        BigDecimal total,
        Instant createdAt
) {
}

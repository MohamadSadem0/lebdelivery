package com.lebanonplatform.modules.orders.dto.response;

import com.lebanonplatform.modules.orders.domain.FulfillmentType;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import com.lebanonplatform.modules.payments.domain.PaymentMethod;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetailsResponse(
        UUID id,
        String orderNumber,
        UUID clientUserId,
        UUID storeId,
        String storeName,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal discount,
        BigDecimal total,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        FulfillmentType fulfillmentType,
        Instant scheduledFor,
        boolean needsChange,
        BigDecimal cashAmountClientHas,
        String notes,
        String addressSnapshot,
        List<OrderItemResponse> items,
        List<OrderTimelineEventResponse> timeline,
        Instant createdAt,
        Instant updatedAt
) {
}

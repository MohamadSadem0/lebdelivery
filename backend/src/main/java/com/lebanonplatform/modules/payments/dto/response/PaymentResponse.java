package com.lebanonplatform.modules.payments.dto.response;

import com.lebanonplatform.modules.payments.domain.PaymentMethod;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID deliveryId,
        PaymentStatus status,
        PaymentMethod method,
        BigDecimal amount,
        String currency,
        UUID collectedByDriverId,
        BigDecimal collectedAmount,
        Instant collectedAt,
        boolean cashMismatch,
        String collectionNote
) {
}

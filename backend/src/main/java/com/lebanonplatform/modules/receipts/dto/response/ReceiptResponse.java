package com.lebanonplatform.modules.receipts.dto.response;

import com.lebanonplatform.modules.payments.domain.PaymentMethod;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import com.lebanonplatform.modules.receipts.domain.ReceiptType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ReceiptResponse(
        UUID id,
        String receiptNumber,
        ReceiptType receiptType,
        UUID orderId,
        String orderNumber,
        UUID storeId,
        String storeName,
        UUID clientUserId,
        String clientName,
        UUID paymentId,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal discount,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String snapshotJson,
        Instant createdAt
) {
}

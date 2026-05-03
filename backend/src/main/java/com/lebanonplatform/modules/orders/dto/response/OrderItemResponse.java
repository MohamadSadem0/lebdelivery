package com.lebanonplatform.modules.orders.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID productId,
        UUID productVariantId,
        String productName,
        String productImage,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}

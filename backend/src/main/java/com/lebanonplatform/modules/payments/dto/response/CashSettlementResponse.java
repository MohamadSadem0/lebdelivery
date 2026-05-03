package com.lebanonplatform.modules.payments.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CashSettlementResponse(
        UUID id,
        UUID driverId,
        String driverName,
        BigDecimal amount,
        String note,
        UUID createdByUserId,
        String createdByUserName,
        Instant createdAt
) {
}

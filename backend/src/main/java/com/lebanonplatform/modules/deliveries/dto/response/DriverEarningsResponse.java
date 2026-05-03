package com.lebanonplatform.modules.deliveries.dto.response;

import java.math.BigDecimal;

public record DriverEarningsResponse(
        long completedDeliveries,
        BigDecimal totalDeliveryFees,
        BigDecimal collectedCashTotal,
        BigDecimal settledCashTotal,
        BigDecimal unsettledCashTotal,
        String note
) {
}

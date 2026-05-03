package com.lebanonplatform.modules.payments.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateCashSettlementRequest(
        @NotNull UUID driverId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        String note
) {
}

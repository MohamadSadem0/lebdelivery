package com.lebanonplatform.modules.payments.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CashCollectionRequest(
        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal collectedAmount,

        Boolean needsChange,

        @Size(max = 500)
        String note
) {
}

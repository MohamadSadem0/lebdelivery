package com.lebanonplatform.modules.servicerequests.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record QuoteServiceRequestRequest(
        @NotNull BigDecimal quotedAmount
) {
}

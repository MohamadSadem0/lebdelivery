package com.lebanonplatform.modules.orders.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record OrderAddressRequest(
        String label,
        @NotBlank String fullAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String phoneNumber
) {
}

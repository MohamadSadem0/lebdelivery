package com.lebanonplatform.modules.clients.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateClientAddressRequest(
        @NotBlank String label,
        @NotBlank String fullAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String phoneNumber,
        String instructions,
        boolean defaultAddress
) {
}

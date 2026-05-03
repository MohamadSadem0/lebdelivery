package com.lebanonplatform.modules.clients.dto.request;

import java.math.BigDecimal;

public record UpdateClientAddressRequest(
        String label,
        String fullAddress,
        BigDecimal latitude,
        BigDecimal longitude,
        String phoneNumber,
        String instructions,
        Boolean defaultAddress
) {
}

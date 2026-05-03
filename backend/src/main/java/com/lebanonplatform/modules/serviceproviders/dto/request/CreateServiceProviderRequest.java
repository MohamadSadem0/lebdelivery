package com.lebanonplatform.modules.serviceproviders.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateServiceProviderRequest(
        @NotBlank String name,
        String phoneNumber,
        String address,
        String city
) {
}

package com.lebanonplatform.modules.serviceproviders.dto.request;

public record UpdateServiceProviderRequest(
        String name,
        String phoneNumber,
        String address,
        String city,
        Boolean active
) {
}

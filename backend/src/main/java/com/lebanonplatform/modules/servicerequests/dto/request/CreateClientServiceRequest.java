package com.lebanonplatform.modules.servicerequests.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.UUID;

public record CreateClientServiceRequest(
        UUID serviceProviderId,
        UUID serviceId,
        @NotBlank String description,
        Map<String, Object> requestedLocation
) {
}

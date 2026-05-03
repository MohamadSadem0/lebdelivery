package com.lebanonplatform.modules.servicerequests.dto.response;

import com.lebanonplatform.modules.servicerequests.domain.ServiceRequestStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceRequestResponse(
        UUID id,
        UUID clientUserId,
        String clientName,
        UUID serviceProviderId,
        String serviceProviderName,
        UUID serviceId,
        String serviceName,
        ServiceRequestStatus status,
        String description,
        String requestedLocationSnapshot,
        BigDecimal quotedAmount,
        Instant createdAt,
        Instant updatedAt
) {
}

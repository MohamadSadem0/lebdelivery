package com.lebanonplatform.modules.support.dto.response;

import com.lebanonplatform.modules.support.domain.SupportTicketStatus;
import java.time.Instant;
import java.util.UUID;

public record SupportTicketResponse(
        UUID id,
        UUID userId,
        String userName,
        String subject,
        String description,
        SupportTicketStatus status,
        UUID relatedOrderId,
        UUID relatedDeliveryId,
        UUID relatedServiceRequestId,
        String adminNote,
        Instant createdAt,
        Instant updatedAt
) {
}

package com.lebanonplatform.modules.support.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateSupportTicketRequest(
        @NotBlank String subject,
        @NotBlank String description,
        UUID relatedOrderId,
        UUID relatedDeliveryId,
        UUID relatedServiceRequestId
) {
}

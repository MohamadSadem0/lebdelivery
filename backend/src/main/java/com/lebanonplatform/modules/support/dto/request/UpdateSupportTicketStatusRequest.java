package com.lebanonplatform.modules.support.dto.request;

import com.lebanonplatform.modules.support.domain.SupportTicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSupportTicketStatusRequest(
        @NotNull SupportTicketStatus status,
        String adminNote
) {
}

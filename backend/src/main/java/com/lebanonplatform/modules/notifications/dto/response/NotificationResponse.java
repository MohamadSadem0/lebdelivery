package com.lebanonplatform.modules.notifications.dto.response;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String title,
        String body,
        Instant readAt,
        Instant createdAt
) {
}

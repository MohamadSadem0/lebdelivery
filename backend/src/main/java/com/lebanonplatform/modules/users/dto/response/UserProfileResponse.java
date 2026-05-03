package com.lebanonplatform.modules.users.dto.response;

import com.lebanonplatform.modules.users.domain.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String fullName,
        String phone,
        String email,
        UserStatus status,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt
) {
}

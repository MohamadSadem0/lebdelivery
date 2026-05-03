package com.lebanonplatform.modules.auth.dto.response;

import com.lebanonplatform.modules.users.domain.UserStatus;
import java.util.List;
import java.util.UUID;

public record CurrentUserResponse(
        UUID id,
        String fullName,
        String phone,
        String email,
        UserStatus status,
        List<RoleResponse> roles
) {
}

package com.lebanonplatform.modules.roles.dto.request;

import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignRoleRequest(
        @NotNull PlatformRole role,
        @NotNull EntityType entityType,
        UUID entityId
) {
}

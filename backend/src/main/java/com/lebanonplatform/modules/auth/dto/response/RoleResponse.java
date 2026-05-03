package com.lebanonplatform.modules.auth.dto.response;

import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        PlatformRole role,
        EntityType entityType,
        UUID entityId
) {
}

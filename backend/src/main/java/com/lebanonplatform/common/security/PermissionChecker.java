package com.lebanonplatform.common.security;

import com.lebanonplatform.modules.roles.domain.PlatformRole;
import org.springframework.stereotype.Component;

@Component
public class PermissionChecker {

    public boolean hasRole(CurrentUser user, PlatformRole role) {
        return user != null && user.roles().contains(role.name());
    }
}

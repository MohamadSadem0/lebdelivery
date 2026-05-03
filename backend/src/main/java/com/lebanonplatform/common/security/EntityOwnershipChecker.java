package com.lebanonplatform.common.security;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class EntityOwnershipChecker {

    private final AuthorizationService authorizationService;

    public EntityOwnershipChecker(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    public boolean canAccessStore(UUID userId, UUID storeId) {
        return authorizationService.canAccessStore(userId, storeId);
    }
}

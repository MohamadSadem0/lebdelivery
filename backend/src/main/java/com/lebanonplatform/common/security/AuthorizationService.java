package com.lebanonplatform.common.security;

import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.roles.repository.UserRoleRepository;
import java.util.Collection;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private final UserRoleRepository userRoleRepository;

    public AuthorizationService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public boolean hasRole(UUID userId, PlatformRole role) {
        return userRoleRepository.existsByUserIdAndRole(userId, role);
    }

    public boolean hasAnyRole(UUID userId, Collection<PlatformRole> roles) {
        return userRoleRepository.existsByUserIdAndRoleIn(userId, roles);
    }

    public boolean hasEntityRole(UUID userId, PlatformRole role, EntityType entityType, UUID entityId) {
        return userRoleRepository.existsByUserIdAndRoleAndEntityTypeAndEntityId(userId, role, entityType, entityId);
    }

    public boolean canAccessStore(UUID userId, UUID storeId) {
        return canManageStore(userId, storeId);
    }

    public boolean canManageStore(UUID userId, UUID storeId) {
        return hasEntityRole(userId, PlatformRole.STORE_OWNER, EntityType.STORE, storeId)
                || hasEntityRole(userId, PlatformRole.STORE_STAFF, EntityType.STORE, storeId)
                || hasRole(userId, PlatformRole.ADMIN);
    }

    public boolean canAccessProvider(UUID userId, UUID providerId) {
        return canManageProvider(userId, providerId);
    }

    public boolean canManageProvider(UUID userId, UUID providerId) {
        return hasEntityRole(userId, PlatformRole.PROVIDER_OWNER, EntityType.SERVICE_PROVIDER, providerId)
                || hasEntityRole(userId, PlatformRole.PROVIDER_STAFF, EntityType.SERVICE_PROVIDER, providerId)
                || hasRole(userId, PlatformRole.ADMIN);
    }

    public boolean canAccessDelivery(UUID userId, UUID deliveryId) {
        // TODO: Resolve delivery ownership through delivery assignment once delivery repositories are implemented.
        return hasAnyRole(userId, java.util.List.of(PlatformRole.ADMIN, PlatformRole.SUPPORT_AGENT));
    }

    public boolean canAccessOrder(UUID userId, UUID orderId) {
        // TODO: Resolve order ownership through client/store/provider relationships once order repositories are implemented.
        return hasAnyRole(userId, java.util.List.of(PlatformRole.ADMIN, PlatformRole.SUPPORT_AGENT));
    }
}

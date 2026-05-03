package com.lebanonplatform.modules.roles.repository;

import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.roles.domain.UserRole;
import com.lebanonplatform.modules.users.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findByUserOrderByRoleAsc(User user);

    List<UserRole> findByUserId(UUID userId);

    boolean existsByUserIdAndRole(UUID userId, PlatformRole role);

    boolean existsByUserIdAndRoleIn(UUID userId, Collection<PlatformRole> roles);

    boolean existsByUserIdAndRoleAndEntityTypeAndEntityId(UUID userId, PlatformRole role, EntityType entityType, UUID entityId);

    Optional<UserRole> findByUserIdAndRoleAndEntityTypeAndEntityId(UUID userId, PlatformRole role, EntityType entityType, UUID entityId);
}

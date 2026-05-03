package com.lebanonplatform.modules.roles.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.auth.dto.response.RoleResponse;
import com.lebanonplatform.modules.roles.domain.UserRole;
import com.lebanonplatform.modules.roles.dto.request.AssignRoleRequest;
import com.lebanonplatform.modules.roles.repository.UserRoleRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleAdministrationService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleAdministrationService(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles(UUID userId) {
        ensureUserExists(userId);
        return userRoleRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RoleResponse assignRole(UUID userId, AssignRoleRequest request) {
        User user = ensureUserExists(userId);
        boolean duplicate = userRoleRepository.findByUserId(userId).stream()
                .anyMatch(role -> role.getRole() == request.role()
                        && role.getEntityType() == request.entityType()
                        && java.util.Objects.equals(role.getEntityId(), request.entityId()));

        if (duplicate) {
            throw new BaseApplicationException("ROLE_ALREADY_ASSIGNED", "This role is already assigned to the user.");
        }

        UserRole role = new UserRole();
        role.setUser(user);
        role.setRole(request.role());
        role.setEntityType(request.entityType());
        role.setEntityId(request.entityId());
        return toResponse(userRoleRepository.save(role));
    }

    @Transactional
    public void deleteRole(UUID userId, UUID roleId) {
        ensureUserExists(userId);
        UserRole role = userRoleRepository.findById(roleId)
                .filter(candidate -> candidate.getUser().getId().equals(userId))
                .orElseThrow(() -> new BaseApplicationException("ROLE_NOT_FOUND", "Role assignment was not found."));
        userRoleRepository.delete(role);
    }

    private User ensureUserExists(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private RoleResponse toResponse(UserRole role) {
        return new RoleResponse(role.getId(), role.getRole(), role.getEntityType(), role.getEntityId());
    }
}

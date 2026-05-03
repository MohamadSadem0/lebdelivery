package com.lebanonplatform.modules.admin.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.auth.dto.response.RoleResponse;
import com.lebanonplatform.modules.roles.application.RoleAdministrationService;
import com.lebanonplatform.modules.roles.dto.request.AssignRoleRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users/{userId}/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserRoleController {

    private final RoleAdministrationService roleAdministrationService;

    public AdminUserRoleController(RoleAdministrationService roleAdministrationService) {
        this.roleAdministrationService = roleAdministrationService;
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> listRoles(@PathVariable UUID userId) {
        return ApiResponse.success(roleAdministrationService.listRoles(userId));
    }

    @PostMapping
    public ApiResponse<RoleResponse> assignRole(@PathVariable UUID userId, @Valid @RequestBody AssignRoleRequest request) {
        return ApiResponse.success("Role assigned.", roleAdministrationService.assignRole(userId, request));
    }

    @DeleteMapping("/{roleId}")
    public ApiResponse<Void> deleteRole(@PathVariable UUID userId, @PathVariable UUID roleId) {
        roleAdministrationService.deleteRole(userId, roleId);
        return ApiResponse.<Void>success("Role removed.", null);
    }
}

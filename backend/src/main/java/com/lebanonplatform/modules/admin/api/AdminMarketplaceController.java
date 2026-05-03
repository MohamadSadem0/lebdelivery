package com.lebanonplatform.modules.admin.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.admin.application.AdminMarketplaceService;
import com.lebanonplatform.modules.admin.dto.request.AdminDecisionRequest;
import com.lebanonplatform.modules.drivers.dto.response.DriverProfileResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreResponse;
import com.lebanonplatform.modules.users.dto.response.UserProfileResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMarketplaceController {

    private final AdminMarketplaceService adminMarketplaceService;

    public AdminMarketplaceController(AdminMarketplaceService adminMarketplaceService) {
        this.adminMarketplaceService = adminMarketplaceService;
    }

    @GetMapping("/users")
    public ApiResponse<List<UserProfileResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminMarketplaceService.listUsers(page, size));
    }

    @PostMapping("/users/{userId}/activate")
    public ApiResponse<UserProfileResponse> activateUser(
            Authentication authentication,
            @PathVariable UUID userId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.activateUser(authentication, userId, request));
    }

    @PostMapping("/users/{userId}/suspend")
    public ApiResponse<UserProfileResponse> suspendUser(
            Authentication authentication,
            @PathVariable UUID userId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.suspendUser(authentication, userId, request));
    }

    @GetMapping("/stores")
    public ApiResponse<List<StoreResponse>> listStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminMarketplaceService.listStores(page, size));
    }

    @PostMapping("/stores/{storeId}/approve")
    public ApiResponse<StoreResponse> approveStore(
            Authentication authentication,
            @PathVariable UUID storeId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.approveStore(authentication, storeId, request));
    }

    @PostMapping("/stores/{storeId}/reject")
    public ApiResponse<StoreResponse> rejectStore(
            Authentication authentication,
            @PathVariable UUID storeId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.rejectStore(authentication, storeId, request));
    }

    @PostMapping("/stores/{storeId}/suspend")
    public ApiResponse<StoreResponse> suspendStore(
            Authentication authentication,
            @PathVariable UUID storeId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.suspendStore(authentication, storeId, request));
    }

    @GetMapping("/drivers")
    public ApiResponse<List<DriverProfileResponse>> listDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminMarketplaceService.listDrivers(page, size));
    }

    @PostMapping("/drivers/{driverId}/approve")
    public ApiResponse<DriverProfileResponse> approveDriver(
            Authentication authentication,
            @PathVariable UUID driverId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.approveDriver(authentication, driverId, request));
    }

    @PostMapping("/drivers/{driverId}/reject")
    public ApiResponse<DriverProfileResponse> rejectDriver(
            Authentication authentication,
            @PathVariable UUID driverId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.rejectDriver(authentication, driverId, request));
    }

    @PostMapping("/drivers/{driverId}/suspend")
    public ApiResponse<DriverProfileResponse> suspendDriver(
            Authentication authentication,
            @PathVariable UUID driverId,
            @RequestBody(required = false) AdminDecisionRequest request
    ) {
        return ApiResponse.success(adminMarketplaceService.suspendDriver(authentication, driverId, request));
    }
}

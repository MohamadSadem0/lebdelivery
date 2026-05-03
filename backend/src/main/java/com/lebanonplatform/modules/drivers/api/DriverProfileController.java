package com.lebanonplatform.modules.drivers.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.drivers.application.DriverService;
import com.lebanonplatform.modules.drivers.dto.request.CreateDriverProfileRequest;
import com.lebanonplatform.modules.drivers.dto.response.DriverProfileResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/driver/profile")
public class DriverProfileController {

    private final DriverService driverService;

    public DriverProfileController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ApiResponse<DriverProfileResponse> createOrUpdate(Authentication authentication, @Valid @RequestBody CreateDriverProfileRequest request) {
        return ApiResponse.success("Driver profile ready.", driverService.createOrUpdateProfile(authentication, request));
    }

    @GetMapping
    public ApiResponse<DriverProfileResponse> getProfile(Authentication authentication) {
        return ApiResponse.success(driverService.getProfile(authentication));
    }
}

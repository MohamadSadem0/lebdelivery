package com.lebanonplatform.modules.users.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.users.application.UserProfileService;
import com.lebanonplatform.modules.users.dto.request.UpdateCurrentUserRequest;
import com.lebanonplatform.modules.users.dto.response.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ApiResponse<UserProfileResponse> getCurrentProfile(Authentication authentication) {
        return ApiResponse.success(userProfileService.getCurrentProfile(authentication));
    }

    @PatchMapping
    public ApiResponse<UserProfileResponse> updateCurrentProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateCurrentUserRequest request
    ) {
        return ApiResponse.success("Profile updated successfully.", userProfileService.updateCurrentProfile(authentication, request));
    }
}

package com.lebanonplatform.modules.auth.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.auth.application.AuthService;
import com.lebanonplatform.modules.auth.dto.request.ActiveRoleRequest;
import com.lebanonplatform.modules.auth.dto.request.LoginRequest;
import com.lebanonplatform.modules.auth.dto.request.LogoutRequest;
import com.lebanonplatform.modules.auth.dto.request.RefreshTokenRequest;
import com.lebanonplatform.modules.auth.dto.request.RegisterRequest;
import com.lebanonplatform.modules.auth.dto.response.ActiveRoleResponse;
import com.lebanonplatform.modules.auth.dto.response.AuthResponse;
import com.lebanonplatform.modules.auth.dto.response.CurrentUserResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success("Registered successfully.", authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Logged in successfully.", authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success("Token refreshed successfully.", authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody(required = false) LogoutRequest request) {
        authService.logout(request == null ? new LogoutRequest(null) : request);
        return ApiResponse.<Void>success("Logged out successfully.", null);
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(Authentication authentication) {
        return ApiResponse.success(authService.getCurrentUser(authentication));
    }

    @PostMapping("/active-role")
    public ApiResponse<ActiveRoleResponse> selectActiveRole(
            Authentication authentication,
            @Valid @RequestBody ActiveRoleRequest request
    ) {
        return ApiResponse.success("Active role selected.", authService.selectActiveRole(authentication, request));
    }
}

package com.lebanonplatform.modules.auth.dto.response;

public record AuthResponse(
        UserSummaryResponse user,
        String accessToken,
        String refreshToken
) {
}

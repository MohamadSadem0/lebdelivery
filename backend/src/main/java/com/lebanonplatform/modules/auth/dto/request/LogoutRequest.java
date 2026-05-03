package com.lebanonplatform.modules.auth.dto.request;

public record LogoutRequest(
        String refreshToken
) {
}

package com.lebanonplatform.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @Email String email,
        @NotBlank @Size(min = 8) String password
) {
}

package com.lebanonplatform.modules.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateCurrentUserRequest(
        @Size(max = 255)
        String fullName,

        @Size(max = 32)
        String phone,

        @Email
        @Size(max = 255)
        String email
) {
}

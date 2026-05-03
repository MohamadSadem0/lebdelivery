package com.lebanonplatform.modules.products.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductAttributeRequest(
        @NotBlank String key,
        @NotBlank String value
) {
}

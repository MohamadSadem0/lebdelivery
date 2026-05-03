package com.lebanonplatform.modules.products.dto.response;

import java.util.UUID;

public record ProductAttributeResponse(
        UUID id,
        String key,
        String value
) {
}

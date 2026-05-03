package com.lebanonplatform.modules.products.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateProductAvailabilityRequest(
        @NotNull @com.fasterxml.jackson.annotation.JsonProperty("isAvailable") Boolean available
) {
}

package com.lebanonplatform.modules.drivers.dto.request;

import com.lebanonplatform.modules.drivers.domain.DriverType;
import jakarta.validation.constraints.NotNull;

public record CreateDriverProfileRequest(
        @NotNull DriverType driverType,
        String vehicleType,
        String phoneNumber
) {
}

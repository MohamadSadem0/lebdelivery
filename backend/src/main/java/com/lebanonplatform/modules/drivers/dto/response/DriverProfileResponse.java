package com.lebanonplatform.modules.drivers.dto.response;

import com.lebanonplatform.modules.drivers.domain.DriverStatus;
import com.lebanonplatform.modules.drivers.domain.DriverType;
import java.time.Instant;
import java.util.UUID;

public record DriverProfileResponse(
        UUID id,
        UUID userId,
        String fullName,
        DriverStatus status,
        DriverType driverType,
        String vehicleType,
        String phoneNumber,
        Instant createdAt,
        Instant updatedAt
) {
}

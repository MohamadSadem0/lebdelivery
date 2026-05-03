package com.lebanonplatform.modules.drivers.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.drivers.domain.Driver;
import com.lebanonplatform.modules.drivers.domain.DriverStatus;
import com.lebanonplatform.modules.drivers.domain.DriverType;
import com.lebanonplatform.modules.drivers.dto.request.CreateDriverProfileRequest;
import com.lebanonplatform.modules.drivers.dto.response.DriverProfileResponse;
import com.lebanonplatform.modules.drivers.repository.DriverRepository;
import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.roles.domain.UserRole;
import com.lebanonplatform.modules.roles.repository.UserRoleRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public DriverProfileResponse createOrUpdateProfile(Authentication authentication, CreateDriverProfileRequest request) {
        User user = currentUser(authentication);
        if (request.driverType() != DriverType.INDEPENDENT) {
            throw new BaseApplicationException("DRIVER_TYPE_NOT_SUPPORTED", "Only independent driver onboarding is supported in this MVP.");
        }

        Driver driver = driverRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            Driver created = new Driver();
            created.setUser(user);
            return created;
        });
        driver.setDriverType(request.driverType());
        driver.setVehicleType(request.vehicleType());
        driver.setPhoneNumber(request.phoneNumber() == null ? user.getPhoneNumber() : request.phoneNumber());
        // MVP auto-activation. Replace with admin approval before production.
        driver.setStatus(DriverStatus.ACTIVE);
        driver = driverRepository.save(driver);

        assignIndependentDriverRole(user, driver.getId());
        return toResponse(driver);
    }

    @Transactional(readOnly = true)
    public DriverProfileResponse getProfile(Authentication authentication) {
        return toResponse(currentDriver(authentication));
    }

    @Transactional(readOnly = true)
    public Driver currentActiveDriver(Authentication authentication) {
        Driver driver = currentDriver(authentication);
        if (driver.getStatus() != DriverStatus.ACTIVE) {
            throw new BaseApplicationException("DRIVER_NOT_ACTIVE", "Driver profile is not active.");
        }
        return driver;
    }

    public DriverProfileResponse toResponse(Driver driver) {
        return new DriverProfileResponse(
                driver.getId(),
                driver.getUser().getId(),
                driver.getUser().getFullName(),
                driver.getStatus(),
                driver.getDriverType(),
                driver.getVehicleType(),
                driver.getPhoneNumber(),
                driver.getCreatedAt(),
                driver.getUpdatedAt()
        );
    }

    private void assignIndependentDriverRole(User user, UUID driverId) {
        if (userRoleRepository.findByUserIdAndRoleAndEntityTypeAndEntityId(user.getId(), PlatformRole.INDEPENDENT_DRIVER, EntityType.DRIVER, driverId).isPresent()) {
            return;
        }
        UserRole role = new UserRole();
        role.setUser(user);
        role.setRole(PlatformRole.INDEPENDENT_DRIVER);
        role.setEntityType(EntityType.DRIVER);
        role.setEntityId(driverId);
        userRoleRepository.save(role);
    }

    private Driver currentDriver(Authentication authentication) {
        User user = currentUser(authentication);
        return driverRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BaseApplicationException("DRIVER_PROFILE_NOT_FOUND", "Driver profile was not found."));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }
}

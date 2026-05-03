package com.lebanonplatform.modules.admin.application;

import com.lebanonplatform.common.audit.AuditService;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.admin.dto.request.AdminDecisionRequest;
import com.lebanonplatform.modules.drivers.application.DriverService;
import com.lebanonplatform.modules.drivers.domain.Driver;
import com.lebanonplatform.modules.drivers.domain.DriverStatus;
import com.lebanonplatform.modules.drivers.dto.response.DriverProfileResponse;
import com.lebanonplatform.modules.drivers.repository.DriverRepository;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.stores.domain.Store;
import com.lebanonplatform.modules.stores.domain.StoreStatus;
import com.lebanonplatform.modules.stores.dto.response.StoreResponse;
import com.lebanonplatform.modules.stores.repository.StoreRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.domain.UserStatus;
import com.lebanonplatform.modules.users.dto.response.UserProfileResponse;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminMarketplaceService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DriverRepository driverRepository;
    private final StoreService storeService;
    private final DriverService driverService;
    private final AuditService auditService;

    public AdminMarketplaceService(
            UserRepository userRepository,
            StoreRepository storeRepository,
            DriverRepository driverRepository,
            StoreService storeService,
            DriverService driverService,
            AuditService auditService
    ) {
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.driverRepository = driverRepository;
        this.storeService = storeService;
        this.driverService = driverService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> listUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(Math.max(0, page), clampSize(size))).stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> listStores(int page, int size) {
        return storeRepository.findAll(PageRequest.of(Math.max(0, page), clampSize(size))).stream()
                .map(storeService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DriverProfileResponse> listDrivers(int page, int size) {
        return driverRepository.findAll(PageRequest.of(Math.max(0, page), clampSize(size))).stream()
                .map(driverService::toResponse)
                .toList();
    }

    @Transactional
    public StoreResponse approveStore(Authentication authentication, UUID storeId, AdminDecisionRequest request) {
        Store store = findStore(storeId);
        store.setStatus(StoreStatus.ACTIVE);
        store = storeRepository.save(store);
        audit(authentication, "ADMIN_STORE_APPROVED", "STORE", storeId, request);
        return storeService.toResponse(store);
    }

    @Transactional
    public StoreResponse rejectStore(Authentication authentication, UUID storeId, AdminDecisionRequest request) {
        Store store = findStore(storeId);
        store.setStatus(StoreStatus.REJECTED);
        store = storeRepository.save(store);
        audit(authentication, "ADMIN_STORE_REJECTED", "STORE", storeId, request);
        return storeService.toResponse(store);
    }

    @Transactional
    public StoreResponse suspendStore(Authentication authentication, UUID storeId, AdminDecisionRequest request) {
        Store store = findStore(storeId);
        store.setStatus(StoreStatus.SUSPENDED);
        store = storeRepository.save(store);
        audit(authentication, "ADMIN_STORE_SUSPENDED", "STORE", storeId, request);
        return storeService.toResponse(store);
    }

    @Transactional
    public DriverProfileResponse approveDriver(Authentication authentication, UUID driverId, AdminDecisionRequest request) {
        Driver driver = findDriver(driverId);
        driver.setStatus(DriverStatus.ACTIVE);
        driver = driverRepository.save(driver);
        audit(authentication, "ADMIN_DRIVER_APPROVED", "DRIVER", driverId, request);
        return driverService.toResponse(driver);
    }

    @Transactional
    public DriverProfileResponse rejectDriver(Authentication authentication, UUID driverId, AdminDecisionRequest request) {
        Driver driver = findDriver(driverId);
        driver.setStatus(DriverStatus.REJECTED);
        driver = driverRepository.save(driver);
        audit(authentication, "ADMIN_DRIVER_REJECTED", "DRIVER", driverId, request);
        return driverService.toResponse(driver);
    }

    @Transactional
    public DriverProfileResponse suspendDriver(Authentication authentication, UUID driverId, AdminDecisionRequest request) {
        Driver driver = findDriver(driverId);
        driver.setStatus(DriverStatus.SUSPENDED);
        driver = driverRepository.save(driver);
        audit(authentication, "ADMIN_DRIVER_SUSPENDED", "DRIVER", driverId, request);
        return driverService.toResponse(driver);
    }

    @Transactional
    public UserProfileResponse activateUser(Authentication authentication, UUID userId, AdminDecisionRequest request) {
        User user = findUser(userId);
        user.setActive(true);
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        audit(authentication, "ADMIN_USER_ACTIVATED", "USER", userId, request);
        return toUserResponse(user);
    }

    @Transactional
    public UserProfileResponse suspendUser(Authentication authentication, UUID userId, AdminDecisionRequest request) {
        User user = findUser(userId);
        user.setActive(false);
        user.setStatus(UserStatus.SUSPENDED);
        user = userRepository.save(user);
        audit(authentication, "ADMIN_USER_SUSPENDED", "USER", userId, request);
        return toUserResponse(user);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private Store findStore(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseApplicationException("STORE_NOT_FOUND", "Store was not found."));
    }

    private Driver findDriver(UUID driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new BaseApplicationException("DRIVER_NOT_FOUND", "Driver was not found."));
    }

    private UserProfileResponse toUserResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getStatus(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private int clampSize(int size) {
        return Math.min(Math.max(1, size), 100);
    }

    private void audit(Authentication authentication, String action, String entityType, UUID entityId, AdminDecisionRequest request) {
        UUID actorId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            actorId = principal.getId();
        }
        String metadata = request == null || request.reason() == null || request.reason().isBlank()
                ? "{}"
                : "{\"reason\":\"" + request.reason().replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
        auditService.record(actorId, action, entityType, entityId, metadata);
    }
}

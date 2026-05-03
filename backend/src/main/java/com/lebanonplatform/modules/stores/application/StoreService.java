package com.lebanonplatform.modules.stores.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.AuthorizationService;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.roles.domain.UserRole;
import com.lebanonplatform.modules.roles.repository.UserRoleRepository;
import com.lebanonplatform.modules.stores.domain.DeliveryMode;
import com.lebanonplatform.modules.stores.domain.Store;
import com.lebanonplatform.modules.stores.domain.StoreStatus;
import com.lebanonplatform.modules.stores.dto.request.CreateStoreRequest;
import com.lebanonplatform.modules.stores.dto.request.UpdateStoreRequest;
import com.lebanonplatform.modules.stores.dto.response.StoreResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreSummaryResponse;
import com.lebanonplatform.modules.stores.repository.StoreRepository;
import com.lebanonplatform.modules.stores.repository.StoreTypeConfigRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final StoreRepository storeRepository;
    private final StoreTypeConfigRepository storeTypeConfigRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthorizationService authorizationService;
    private final StoreHoursService storeHoursService;
    private final ObjectMapper objectMapper;

    public StoreService(
            StoreRepository storeRepository,
            StoreTypeConfigRepository storeTypeConfigRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            AuthorizationService authorizationService,
            StoreHoursService storeHoursService,
            ObjectMapper objectMapper
    ) {
        this.storeRepository = storeRepository;
        this.storeTypeConfigRepository = storeTypeConfigRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.authorizationService = authorizationService;
        this.storeHoursService = storeHoursService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public StoreResponse createStore(Authentication authentication, CreateStoreRequest request) {
        User user = currentUser(authentication);
        storeTypeConfigRepository.findByStoreTypeCode(request.storeTypeCode())
                .orElseThrow(() -> new BaseApplicationException("STORE_TYPE_NOT_SUPPORTED", "Store type is not supported."));

        Store store = new Store();
        store.setOwner(user);
        store.setName(request.name());
        store.setDescription(request.description());
        store.setStoreTypeCode(request.storeTypeCode());
        store.setPhoneNumber(request.phone());
        store.setAddress(request.address());
        store.setLatitude(request.latitude());
        store.setLongitude(request.longitude());
        store.setDeliveryMode(request.deliveryMode() == null ? DeliveryMode.BOTH : request.deliveryMode());
        store.setOpeningHoursJson(writeMap(request.openingHours()));
        store.setMinimumOrderAmount(request.minimumOrderAmount());
        store.setAveragePreparationMinutes(request.averagePreparationMinutes());
        store.setStatus(StoreStatus.PENDING_APPROVAL);
        store = storeRepository.save(store);

        UserRole ownerRole = new UserRole();
        ownerRole.setUser(user);
        ownerRole.setRole(PlatformRole.STORE_OWNER);
        ownerRole.setEntityType(EntityType.STORE);
        ownerRole.setEntityId(store.getId());
        userRoleRepository.save(ownerRole);

        return toResponse(store);
    }

    @Transactional(readOnly = true)
    public List<StoreSummaryResponse> listOwnedStores(Authentication authentication) {
        User user = currentUser(authentication);
        return storeRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreResponse getOwnedStore(Authentication authentication, UUID storeId) {
        ensureCanManageStore(authentication, storeId);
        return toResponse(findStore(storeId));
    }

    @Transactional
    public StoreResponse updateStore(Authentication authentication, UUID storeId, UpdateStoreRequest request) {
        ensureCanManageStore(authentication, storeId);
        Store store = findStore(storeId);

        if (request.name() != null) {
            store.setName(request.name());
        }
        if (request.description() != null) {
            store.setDescription(request.description());
        }
        if (request.phone() != null) {
            store.setPhoneNumber(request.phone());
        }
        if (request.address() != null) {
            store.setAddress(request.address());
        }
        if (request.latitude() != null) {
            store.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            store.setLongitude(request.longitude());
        }
        if (request.deliveryMode() != null) {
            store.setDeliveryMode(request.deliveryMode());
        }
        if (request.openingHours() != null) {
            store.setOpeningHoursJson(writeMap(request.openingHours()));
        }
        if (request.minimumOrderAmount() != null) {
            store.setMinimumOrderAmount(request.minimumOrderAmount());
        }
        if (request.averagePreparationMinutes() != null) {
            store.setAveragePreparationMinutes(request.averagePreparationMinutes());
        }

        return toResponse(storeRepository.save(store));
    }

    @Transactional(readOnly = true)
    public List<StoreSummaryResponse> listPublicStores(int page, int size) {
        return storeRepository.findByStatusOrderByCreatedAtDesc(StoreStatus.ACTIVE, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreResponse getPublicStore(UUID storeId) {
        Store store = findStore(storeId);
        if (store.getStatus() != StoreStatus.ACTIVE) {
            throw new BaseApplicationException("STORE_NOT_FOUND", "Store was not found.");
        }
        return toResponse(store);
    }

    public void ensureCanManageStore(Authentication authentication, UUID storeId) {
        UUID userId = currentUser(authentication).getId();
        if (!authorizationService.canManageStore(userId, storeId)) {
            throw new BaseApplicationException("STORE_ACCESS_DENIED", "You do not have access to manage this store.");
        }
    }

    public Store findStore(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseApplicationException("STORE_NOT_FOUND", "Store was not found."));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    public StoreResponse toResponse(Store store) {
        return new StoreResponse(
                store.getId(),
                store.getOwner() == null ? null : store.getOwner().getId(),
                store.getName(),
                store.getDescription(),
                store.getStoreTypeCode(),
                store.getPhoneNumber(),
                store.getAddress(),
                store.getLatitude(),
                store.getLongitude(),
                store.getDeliveryMode(),
                store.getStatus(),
                storeHoursService.isOpenNow(store),
                readMap(store.getOpeningHoursJson()),
                store.getMinimumOrderAmount(),
                store.getAveragePreparationMinutes(),
                store.getRatingAverage(),
                store.getRatingCount(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }

    public StoreSummaryResponse toSummary(Store store) {
        return new StoreSummaryResponse(
                store.getId(),
                store.getName(),
                store.getDescription(),
                store.getStoreTypeCode(),
                store.getPhoneNumber(),
                store.getAddress(),
                store.getDeliveryMode(),
                store.getStatus(),
                storeHoursService.isOpenNow(store),
                store.getRatingAverage(),
                store.getRatingCount()
        );
    }

    private String writeMap(Map<String, Object> value) {
        if (value == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new BaseApplicationException("INVALID_OPENING_HOURS", "Opening hours format is invalid.");
        }
    }

    private Map<String, Object> readMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (Exception exception) {
            return Map.of();
        }
    }
}

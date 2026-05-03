package com.lebanonplatform.modules.serviceproviders.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.AuthorizationService;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.roles.domain.EntityType;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.roles.domain.UserRole;
import com.lebanonplatform.modules.roles.repository.UserRoleRepository;
import com.lebanonplatform.modules.serviceproviders.domain.ServiceProvider;
import com.lebanonplatform.modules.serviceproviders.dto.request.CreateServiceProviderRequest;
import com.lebanonplatform.modules.serviceproviders.dto.request.UpdateServiceProviderRequest;
import com.lebanonplatform.modules.serviceproviders.dto.response.ServiceProviderResponse;
import com.lebanonplatform.modules.serviceproviders.repository.ServiceProviderRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final AuthorizationService authorizationService;

    public ServiceProviderService(
            ServiceProviderRepository serviceProviderRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            AuthorizationService authorizationService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional
    public ServiceProviderResponse createProvider(Authentication authentication, CreateServiceProviderRequest request) {
        User owner = currentUser(authentication);
        ServiceProvider provider = new ServiceProvider();
        provider.setOwner(owner);
        provider.setName(request.name());
        provider.setPhoneNumber(request.phoneNumber());
        provider.setAddress(request.address());
        provider.setCity(request.city());
        provider.setActive(true);
        provider = serviceProviderRepository.save(provider);

        UserRole role = new UserRole();
        role.setUser(owner);
        role.setRole(PlatformRole.PROVIDER_OWNER);
        role.setEntityType(EntityType.SERVICE_PROVIDER);
        role.setEntityId(provider.getId());
        userRoleRepository.save(role);

        return toResponse(provider);
    }

    @Transactional(readOnly = true)
    public List<ServiceProviderResponse> listOwnedProviders(Authentication authentication) {
        User owner = currentUser(authentication);
        return serviceProviderRepository.findByOwner_IdOrderByCreatedAtDesc(owner.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceProviderResponse getOwnedProvider(Authentication authentication, UUID providerId) {
        ensureCanManageProvider(authentication, providerId);
        return toResponse(findProvider(providerId));
    }

    @Transactional
    public ServiceProviderResponse updateProvider(Authentication authentication, UUID providerId, UpdateServiceProviderRequest request) {
        ensureCanManageProvider(authentication, providerId);
        ServiceProvider provider = findProvider(providerId);
        if (request.name() != null) {
            provider.setName(request.name());
        }
        if (request.phoneNumber() != null) {
            provider.setPhoneNumber(request.phoneNumber());
        }
        if (request.address() != null) {
            provider.setAddress(request.address());
        }
        if (request.city() != null) {
            provider.setCity(request.city());
        }
        if (request.active() != null) {
            provider.setActive(request.active());
        }
        return toResponse(serviceProviderRepository.save(provider));
    }

    @Transactional(readOnly = true)
    public List<ServiceProviderResponse> listPublicProviders(int page, int size) {
        return serviceProviderRepository.findByActiveTrueOrderByCreatedAtDesc(PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceProviderResponse getPublicProvider(UUID providerId) {
        ServiceProvider provider = findProvider(providerId);
        if (!provider.isActive()) {
            throw new BaseApplicationException("PROVIDER_NOT_FOUND", "Service provider was not found.");
        }
        return toResponse(provider);
    }

    public void ensureCanManageProvider(Authentication authentication, UUID providerId) {
        UUID userId = currentUser(authentication).getId();
        if (!authorizationService.canManageProvider(userId, providerId)) {
            throw new BaseApplicationException("PROVIDER_ACCESS_DENIED", "You do not have access to manage this provider.");
        }
    }

    public ServiceProvider findProvider(UUID providerId) {
        return serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new BaseApplicationException("PROVIDER_NOT_FOUND", "Service provider was not found."));
    }

    public ServiceProviderResponse toResponse(ServiceProvider provider) {
        return new ServiceProviderResponse(
                provider.getId(),
                provider.getOwner() == null ? null : provider.getOwner().getId(),
                provider.getName(),
                provider.getPhoneNumber(),
                provider.getAddress(),
                provider.getCity(),
                provider.isActive(),
                provider.getCreatedAt(),
                provider.getUpdatedAt()
        );
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }
}

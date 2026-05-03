package com.lebanonplatform.modules.clients.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.AuthorizationService;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.clients.domain.ClientAddress;
import com.lebanonplatform.modules.clients.dto.request.CreateClientAddressRequest;
import com.lebanonplatform.modules.clients.dto.request.UpdateClientAddressRequest;
import com.lebanonplatform.modules.clients.dto.response.ClientAddressResponse;
import com.lebanonplatform.modules.clients.repository.ClientAddressRepository;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ClientAddressService {

    private final ClientAddressRepository clientAddressRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    public ClientAddressService(
            ClientAddressRepository clientAddressRepository,
            UserRepository userRepository,
            AuthorizationService authorizationService
    ) {
        this.clientAddressRepository = clientAddressRepository;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
    }

    @Transactional(readOnly = true)
    public List<ClientAddressResponse> list(Authentication authentication) {
        User user = currentClient(authentication);
        return clientAddressRepository.findByUser_IdAndActiveTrueOrderByCreatedAtDesc(user.getId()).stream()
                .sorted(Comparator.comparing(ClientAddress::isDefaultAddress).reversed()
                        .thenComparing(ClientAddress::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientAddressResponse get(Authentication authentication, UUID addressId) {
        User user = currentClient(authentication);
        return toResponse(findOwnedAddress(user.getId(), addressId));
    }

    @Transactional
    public ClientAddressResponse create(Authentication authentication, CreateClientAddressRequest request) {
        User user = currentClient(authentication);
        boolean shouldBeDefault = request.defaultAddress() || !clientAddressRepository.existsByUser_IdAndActiveTrue(user.getId());
        if (shouldBeDefault) {
            clearDefault(user.getId());
        }

        ClientAddress address = new ClientAddress();
        address.setUser(user);
        address.setLabel(request.label().trim());
        address.setFullAddress(request.fullAddress().trim());
        address.setLatitude(request.latitude());
        address.setLongitude(request.longitude());
        address.setPhoneNumber(trimToNull(request.phoneNumber()));
        address.setInstructions(trimToNull(request.instructions()));
        address.setDefaultAddress(shouldBeDefault);
        return toResponse(clientAddressRepository.save(address));
    }

    @Transactional
    public ClientAddressResponse update(Authentication authentication, UUID addressId, UpdateClientAddressRequest request) {
        User user = currentClient(authentication);
        ClientAddress address = findOwnedAddress(user.getId(), addressId);

        if (StringUtils.hasText(request.label())) {
            address.setLabel(request.label().trim());
        }
        if (StringUtils.hasText(request.fullAddress())) {
            address.setFullAddress(request.fullAddress().trim());
        }
        if (request.latitude() != null) {
            address.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            address.setLongitude(request.longitude());
        }
        if (request.phoneNumber() != null) {
            address.setPhoneNumber(trimToNull(request.phoneNumber()));
        }
        if (request.instructions() != null) {
            address.setInstructions(trimToNull(request.instructions()));
        }
        if (Boolean.TRUE.equals(request.defaultAddress())) {
            clearDefault(user.getId());
            address.setDefaultAddress(true);
        }

        return toResponse(clientAddressRepository.save(address));
    }

    @Transactional
    public ClientAddressResponse setDefault(Authentication authentication, UUID addressId) {
        User user = currentClient(authentication);
        ClientAddress address = findOwnedAddress(user.getId(), addressId);
        clearDefault(user.getId());
        address.setDefaultAddress(true);
        return toResponse(clientAddressRepository.save(address));
    }

    @Transactional
    public void delete(Authentication authentication, UUID addressId) {
        User user = currentClient(authentication);
        ClientAddress address = findOwnedAddress(user.getId(), addressId);
        boolean wasDefault = address.isDefaultAddress();
        address.setActive(false);
        address.setDefaultAddress(false);
        clientAddressRepository.save(address);

        if (wasDefault) {
            clientAddressRepository.findByUser_IdAndActiveTrueOrderByCreatedAtDesc(user.getId()).stream()
                    .findFirst()
                    .ifPresent(nextDefault -> {
                        nextDefault.setDefaultAddress(true);
                        clientAddressRepository.save(nextDefault);
                    });
        }
    }

    public ClientAddressResponse toResponse(ClientAddress address) {
        return new ClientAddressResponse(
                address.getId(),
                address.getLabel(),
                address.getFullAddress(),
                address.getLatitude(),
                address.getLongitude(),
                address.getPhoneNumber(),
                address.getInstructions(),
                address.isDefaultAddress(),
                address.getCreatedAt(),
                address.getUpdatedAt()
        );
    }

    private ClientAddress findOwnedAddress(UUID userId, UUID addressId) {
        return clientAddressRepository.findByIdAndUser_IdAndActiveTrue(addressId, userId)
                .orElseThrow(() -> new BaseApplicationException("CLIENT_ADDRESS_NOT_FOUND", "Client address was not found."));
    }

    private void clearDefault(UUID userId) {
        clientAddressRepository.findByUser_IdAndActiveTrueOrderByCreatedAtDesc(userId).forEach(address -> {
            if (address.isDefaultAddress()) {
                address.setDefaultAddress(false);
                clientAddressRepository.save(address);
            }
        });
    }

    private User currentClient(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
        if (!authorizationService.hasRole(user.getId(), PlatformRole.CLIENT)) {
            throw new BaseApplicationException("CLIENT_ACCESS_DENIED", "Client role is required.");
        }
        return user;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}

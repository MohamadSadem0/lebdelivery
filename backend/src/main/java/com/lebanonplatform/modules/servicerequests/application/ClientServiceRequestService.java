package com.lebanonplatform.modules.servicerequests.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.AuthorizationService;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.serviceproviders.domain.ServiceProvider;
import com.lebanonplatform.modules.serviceproviders.repository.ServiceProviderRepository;
import com.lebanonplatform.modules.servicerequests.domain.ServiceRequest;
import com.lebanonplatform.modules.servicerequests.domain.ServiceRequestStatus;
import com.lebanonplatform.modules.servicerequests.dto.request.CreateClientServiceRequest;
import com.lebanonplatform.modules.servicerequests.dto.response.ServiceRequestResponse;
import com.lebanonplatform.modules.servicerequests.repository.ServiceRequestRepository;
import com.lebanonplatform.modules.services.application.ServiceCatalogService;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCatalogService serviceCatalogService;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final ServiceRequestStatusService statusService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public ClientServiceRequestService(
            ServiceRequestRepository serviceRequestRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServiceCatalogService serviceCatalogService,
            UserRepository userRepository,
            AuthorizationService authorizationService,
            ServiceRequestStatusService statusService,
            NotificationService notificationService,
            ObjectMapper objectMapper
    ) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCatalogService = serviceCatalogService;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
        this.statusService = statusService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ServiceRequestResponse createRequest(Authentication authentication, CreateClientServiceRequest request) {
        User client = currentUser(authentication);
        requireClient(client.getId());

        ServiceProvider provider = null;
        com.lebanonplatform.modules.services.domain.Service service = null;
        if (request.serviceId() != null) {
            service = serviceCatalogService.findService(request.serviceId());
            if (!service.isActive() || !service.getServiceProvider().isActive()) {
                throw new BaseApplicationException("SERVICE_NOT_AVAILABLE", "Service is not available.");
            }
            provider = service.getServiceProvider();
        } else if (request.serviceProviderId() != null) {
            provider = serviceProviderRepository.findById(request.serviceProviderId())
                    .orElseThrow(() -> new BaseApplicationException("PROVIDER_NOT_FOUND", "Service provider was not found."));
            if (!provider.isActive()) {
                throw new BaseApplicationException("PROVIDER_NOT_FOUND", "Service provider was not found.");
            }
        }

        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setClient(client);
        serviceRequest.setServiceProvider(provider);
        serviceRequest.setService(service);
        serviceRequest.setStatus(provider == null ? ServiceRequestStatus.PENDING : ServiceRequestStatus.WAITING_FOR_QUOTE);
        serviceRequest.setDescription(request.description());
        serviceRequest.setRequestedLocationSnapshot(writeSnapshot(request.requestedLocation()));
        serviceRequest = serviceRequestRepository.save(serviceRequest);
        notificationService.create(provider == null || provider.getOwner() == null ? null : provider.getOwner().getId(), "New service request", "A client sent a new service request.");
        return toResponse(serviceRequest);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> listClientRequests(Authentication authentication, int page, int size) {
        User client = currentUser(authentication);
        requireClient(client.getId());
        return serviceRequestRepository.findByClient_IdOrderByCreatedAtDesc(client.getId(), PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse getClientRequest(Authentication authentication, UUID requestId) {
        User client = currentUser(authentication);
        requireClient(client.getId());
        return toResponse(serviceRequestRepository.findByIdAndClient_Id(requestId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("SERVICE_REQUEST_NOT_FOUND", "Service request was not found.")));
    }

    @Transactional
    public ServiceRequestResponse acceptQuote(Authentication authentication, UUID requestId) {
        User client = currentUser(authentication);
        requireClient(client.getId());
        ServiceRequest serviceRequest = serviceRequestRepository.findByIdAndClient_Id(requestId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("SERVICE_REQUEST_NOT_FOUND", "Service request was not found."));
        statusService.requireTransition(serviceRequest.getStatus(), ServiceRequestStatus.QUOTE_ACCEPTED);
        serviceRequest.setStatus(ServiceRequestStatus.QUOTE_ACCEPTED);
        serviceRequest = serviceRequestRepository.save(serviceRequest);
        notificationService.create(serviceRequest.getServiceProvider() == null || serviceRequest.getServiceProvider().getOwner() == null ? null : serviceRequest.getServiceProvider().getOwner().getId(), "Quote accepted", "A client accepted your service quote.");
        return toResponse(serviceRequest);
    }

    @Transactional
    public ServiceRequestResponse cancel(Authentication authentication, UUID requestId) {
        User client = currentUser(authentication);
        requireClient(client.getId());
        ServiceRequest serviceRequest = serviceRequestRepository.findByIdAndClient_Id(requestId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("SERVICE_REQUEST_NOT_FOUND", "Service request was not found."));
        statusService.requireTransition(serviceRequest.getStatus(), ServiceRequestStatus.CANCELLED);
        serviceRequest.setStatus(ServiceRequestStatus.CANCELLED);
        serviceRequest = serviceRequestRepository.save(serviceRequest);
        notificationService.create(serviceRequest.getServiceProvider() == null || serviceRequest.getServiceProvider().getOwner() == null ? null : serviceRequest.getServiceProvider().getOwner().getId(), "Service request cancelled", "A client cancelled a service request.");
        return toResponse(serviceRequest);
    }

    public ServiceRequestResponse toResponse(ServiceRequest serviceRequest) {
        return new ServiceRequestResponse(
                serviceRequest.getId(),
                serviceRequest.getClient().getId(),
                serviceRequest.getClient().getFullName(),
                serviceRequest.getServiceProvider() == null ? null : serviceRequest.getServiceProvider().getId(),
                serviceRequest.getServiceProvider() == null ? null : serviceRequest.getServiceProvider().getName(),
                serviceRequest.getService() == null ? null : serviceRequest.getService().getId(),
                serviceRequest.getService() == null ? null : serviceRequest.getService().getName(),
                serviceRequest.getStatus(),
                serviceRequest.getDescription(),
                serviceRequest.getRequestedLocationSnapshot(),
                serviceRequest.getQuotedAmount(),
                serviceRequest.getCreatedAt(),
                serviceRequest.getUpdatedAt()
        );
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private void requireClient(UUID userId) {
        if (!authorizationService.hasRole(userId, PlatformRole.CLIENT)) {
            throw new BaseApplicationException("SERVICE_REQUEST_ACCESS_DENIED", "Client role is required.");
        }
    }

    private String writeSnapshot(Object value) {
        if (value == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new BaseApplicationException("INVALID_SERVICE_REQUEST_LOCATION", "Requested location is invalid.");
        }
    }
}

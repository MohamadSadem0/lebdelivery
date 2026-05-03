package com.lebanonplatform.modules.servicerequests.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.serviceproviders.application.ServiceProviderService;
import com.lebanonplatform.modules.servicerequests.domain.ServiceRequest;
import com.lebanonplatform.modules.servicerequests.domain.ServiceRequestStatus;
import com.lebanonplatform.modules.servicerequests.dto.request.QuoteServiceRequestRequest;
import com.lebanonplatform.modules.servicerequests.dto.request.RejectServiceRequestRequest;
import com.lebanonplatform.modules.servicerequests.dto.response.ServiceRequestResponse;
import com.lebanonplatform.modules.servicerequests.repository.ServiceRequestRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProviderServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceProviderService serviceProviderService;
    private final ClientServiceRequestService clientServiceRequestService;
    private final ServiceRequestStatusService statusService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ProviderServiceRequestService(
            ServiceRequestRepository serviceRequestRepository,
            ServiceProviderService serviceProviderService,
            ClientServiceRequestService clientServiceRequestService,
            ServiceRequestStatusService statusService,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceProviderService = serviceProviderService;
        this.clientServiceRequestService = clientServiceRequestService;
        this.statusService = statusService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> listProviderRequests(Authentication authentication, UUID providerId, int page, int size) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        return serviceRequestRepository.findByServiceProvider_IdOrderByCreatedAtDesc(providerId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(clientServiceRequestService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse getProviderRequest(Authentication authentication, UUID providerId, UUID requestId) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        return clientServiceRequestService.toResponse(findRequest(providerId, requestId));
    }

    @Transactional
    public ServiceRequestResponse accept(Authentication authentication, UUID providerId, UUID requestId) {
        return transition(authentication, providerId, requestId, ServiceRequestStatus.ACCEPTED);
    }

    @Transactional
    public ServiceRequestResponse reject(Authentication authentication, UUID providerId, UUID requestId, RejectServiceRequestRequest request) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        ServiceRequest serviceRequest = findRequest(providerId, requestId);
        statusService.requireTransition(serviceRequest.getStatus(), ServiceRequestStatus.REJECTED);
        serviceRequest.setStatus(ServiceRequestStatus.REJECTED);
        serviceRequest = serviceRequestRepository.save(serviceRequest);
        notificationService.create(serviceRequest.getClient().getId(), "Service request rejected", request == null || request.reason() == null ? "Your service request was rejected." : request.reason());
        return clientServiceRequestService.toResponse(serviceRequest);
    }

    @Transactional
    public ServiceRequestResponse sendQuote(Authentication authentication, UUID providerId, UUID requestId, QuoteServiceRequestRequest request) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        ServiceRequest serviceRequest = findRequest(providerId, requestId);
        statusService.requireTransition(serviceRequest.getStatus(), ServiceRequestStatus.QUOTE_SENT);
        serviceRequest.setQuotedAmount(request.quotedAmount());
        serviceRequest.setStatus(ServiceRequestStatus.QUOTE_SENT);
        serviceRequest = serviceRequestRepository.save(serviceRequest);
        notificationService.create(serviceRequest.getClient().getId(), "Quote received", "A provider sent a quote for your service request.");
        return clientServiceRequestService.toResponse(serviceRequest);
    }

    @Transactional
    public ServiceRequestResponse markInProgress(Authentication authentication, UUID providerId, UUID requestId) {
        return transition(authentication, providerId, requestId, ServiceRequestStatus.IN_PROGRESS);
    }

    @Transactional
    public ServiceRequestResponse markReady(Authentication authentication, UUID providerId, UUID requestId) {
        return transition(authentication, providerId, requestId, ServiceRequestStatus.READY);
    }

    @Transactional
    public ServiceRequestResponse markCompleted(Authentication authentication, UUID providerId, UUID requestId) {
        return transition(authentication, providerId, requestId, ServiceRequestStatus.COMPLETED);
    }

    private ServiceRequestResponse transition(Authentication authentication, UUID providerId, UUID requestId, ServiceRequestStatus next) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        currentUser(authentication);
        ServiceRequest serviceRequest = findRequest(providerId, requestId);
        statusService.requireTransition(serviceRequest.getStatus(), next);
        serviceRequest.setStatus(next);
        serviceRequest = serviceRequestRepository.save(serviceRequest);
        notificationService.create(serviceRequest.getClient().getId(), titleFor(next), "Your service request is now " + next.name().replace('_', ' ') + ".");
        return clientServiceRequestService.toResponse(serviceRequest);
    }

    private ServiceRequest findRequest(UUID providerId, UUID requestId) {
        return serviceRequestRepository.findByIdAndServiceProvider_Id(requestId, providerId)
                .orElseThrow(() -> new BaseApplicationException("SERVICE_REQUEST_NOT_FOUND", "Service request was not found."));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private String titleFor(ServiceRequestStatus status) {
        return switch (status) {
            case ACCEPTED -> "Service request accepted";
            case IN_PROGRESS -> "Service in progress";
            case READY -> "Service ready";
            case COMPLETED -> "Service completed";
            default -> "Service request updated";
        };
    }
}

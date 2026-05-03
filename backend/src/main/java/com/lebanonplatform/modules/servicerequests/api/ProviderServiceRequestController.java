package com.lebanonplatform.modules.servicerequests.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.servicerequests.application.ProviderServiceRequestService;
import com.lebanonplatform.modules.servicerequests.dto.request.QuoteServiceRequestRequest;
import com.lebanonplatform.modules.servicerequests.dto.request.RejectServiceRequestRequest;
import com.lebanonplatform.modules.servicerequests.dto.response.ServiceRequestResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/provider-owner/providers/{providerId}/service-requests")
@PreAuthorize("hasAnyRole('PROVIDER_OWNER','PROVIDER_STAFF','ADMIN')")
public class ProviderServiceRequestController {

    private final ProviderServiceRequestService providerServiceRequestService;

    public ProviderServiceRequestController(ProviderServiceRequestService providerServiceRequestService) {
        this.providerServiceRequestService = providerServiceRequestService;
    }

    @GetMapping
    public ApiResponse<List<ServiceRequestResponse>> listRequests(
            Authentication authentication,
            @PathVariable UUID providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(providerServiceRequestService.listProviderRequests(authentication, providerId, page, size));
    }

    @GetMapping("/{requestId}")
    public ApiResponse<ServiceRequestResponse> getRequest(Authentication authentication, @PathVariable UUID providerId, @PathVariable UUID requestId) {
        return ApiResponse.success(providerServiceRequestService.getProviderRequest(authentication, providerId, requestId));
    }

    @PostMapping("/{requestId}/accept")
    public ApiResponse<ServiceRequestResponse> accept(Authentication authentication, @PathVariable UUID providerId, @PathVariable UUID requestId) {
        return ApiResponse.success(providerServiceRequestService.accept(authentication, providerId, requestId));
    }

    @PostMapping("/{requestId}/reject")
    public ApiResponse<ServiceRequestResponse> reject(
            Authentication authentication,
            @PathVariable UUID providerId,
            @PathVariable UUID requestId,
            @RequestBody(required = false) RejectServiceRequestRequest request
    ) {
        return ApiResponse.success(providerServiceRequestService.reject(authentication, providerId, requestId, request));
    }

    @PostMapping("/{requestId}/quote")
    public ApiResponse<ServiceRequestResponse> sendQuote(
            Authentication authentication,
            @PathVariable UUID providerId,
            @PathVariable UUID requestId,
            @Valid @RequestBody QuoteServiceRequestRequest request
    ) {
        return ApiResponse.success(providerServiceRequestService.sendQuote(authentication, providerId, requestId, request));
    }

    @PostMapping("/{requestId}/mark-in-progress")
    public ApiResponse<ServiceRequestResponse> markInProgress(Authentication authentication, @PathVariable UUID providerId, @PathVariable UUID requestId) {
        return ApiResponse.success(providerServiceRequestService.markInProgress(authentication, providerId, requestId));
    }

    @PostMapping("/{requestId}/mark-ready")
    public ApiResponse<ServiceRequestResponse> markReady(Authentication authentication, @PathVariable UUID providerId, @PathVariable UUID requestId) {
        return ApiResponse.success(providerServiceRequestService.markReady(authentication, providerId, requestId));
    }

    @PostMapping("/{requestId}/mark-completed")
    public ApiResponse<ServiceRequestResponse> markCompleted(Authentication authentication, @PathVariable UUID providerId, @PathVariable UUID requestId) {
        return ApiResponse.success(providerServiceRequestService.markCompleted(authentication, providerId, requestId));
    }
}

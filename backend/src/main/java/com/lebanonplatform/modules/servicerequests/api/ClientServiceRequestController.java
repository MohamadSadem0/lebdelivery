package com.lebanonplatform.modules.servicerequests.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.servicerequests.application.ClientServiceRequestService;
import com.lebanonplatform.modules.servicerequests.dto.request.CreateClientServiceRequest;
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
@RequestMapping("/api/v1/client/service-requests")
@PreAuthorize("hasRole('CLIENT')")
public class ClientServiceRequestController {

    private final ClientServiceRequestService clientServiceRequestService;

    public ClientServiceRequestController(ClientServiceRequestService clientServiceRequestService) {
        this.clientServiceRequestService = clientServiceRequestService;
    }

    @PostMapping
    public ApiResponse<ServiceRequestResponse> createRequest(Authentication authentication, @Valid @RequestBody CreateClientServiceRequest request) {
        return ApiResponse.success("Service request created.", clientServiceRequestService.createRequest(authentication, request));
    }

    @GetMapping
    public ApiResponse<List<ServiceRequestResponse>> listRequests(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(clientServiceRequestService.listClientRequests(authentication, page, size));
    }

    @GetMapping("/{requestId}")
    public ApiResponse<ServiceRequestResponse> getRequest(Authentication authentication, @PathVariable UUID requestId) {
        return ApiResponse.success(clientServiceRequestService.getClientRequest(authentication, requestId));
    }

    @PostMapping("/{requestId}/accept-quote")
    public ApiResponse<ServiceRequestResponse> acceptQuote(Authentication authentication, @PathVariable UUID requestId) {
        return ApiResponse.success(clientServiceRequestService.acceptQuote(authentication, requestId));
    }

    @PostMapping("/{requestId}/cancel")
    public ApiResponse<ServiceRequestResponse> cancel(Authentication authentication, @PathVariable UUID requestId) {
        return ApiResponse.success(clientServiceRequestService.cancel(authentication, requestId));
    }
}

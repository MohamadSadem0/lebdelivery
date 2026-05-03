package com.lebanonplatform.modules.serviceproviders.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.serviceproviders.application.ServiceProviderService;
import com.lebanonplatform.modules.serviceproviders.dto.request.CreateServiceProviderRequest;
import com.lebanonplatform.modules.serviceproviders.dto.request.UpdateServiceProviderRequest;
import com.lebanonplatform.modules.serviceproviders.dto.response.ServiceProviderResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/provider-owner/providers")
@PreAuthorize("hasAnyRole('PROVIDER_OWNER','PROVIDER_STAFF','ADMIN','CLIENT')")
public class ProviderOwnerController {

    private final ServiceProviderService serviceProviderService;

    public ProviderOwnerController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @PostMapping
    public ApiResponse<ServiceProviderResponse> createProvider(Authentication authentication, @Valid @RequestBody CreateServiceProviderRequest request) {
        return ApiResponse.success("Provider created.", serviceProviderService.createProvider(authentication, request));
    }

    @GetMapping
    public ApiResponse<List<ServiceProviderResponse>> listProviders(Authentication authentication) {
        return ApiResponse.success(serviceProviderService.listOwnedProviders(authentication));
    }

    @GetMapping("/{providerId}")
    public ApiResponse<ServiceProviderResponse> getProvider(Authentication authentication, @PathVariable UUID providerId) {
        return ApiResponse.success(serviceProviderService.getOwnedProvider(authentication, providerId));
    }

    @PatchMapping("/{providerId}")
    public ApiResponse<ServiceProviderResponse> updateProvider(
            Authentication authentication,
            @PathVariable UUID providerId,
            @RequestBody UpdateServiceProviderRequest request
    ) {
        return ApiResponse.success(serviceProviderService.updateProvider(authentication, providerId, request));
    }
}

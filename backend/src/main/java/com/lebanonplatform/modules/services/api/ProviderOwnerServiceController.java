package com.lebanonplatform.modules.services.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.services.application.ServiceCatalogService;
import com.lebanonplatform.modules.services.dto.request.CreateServiceRequest;
import com.lebanonplatform.modules.services.dto.request.UpdateServiceRequest;
import com.lebanonplatform.modules.services.dto.response.ServiceResponse;
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
@RequestMapping("/api/v1/provider-owner/providers/{providerId}/services")
@PreAuthorize("hasAnyRole('PROVIDER_OWNER','PROVIDER_STAFF','ADMIN')")
public class ProviderOwnerServiceController {

    private final ServiceCatalogService serviceCatalogService;

    public ProviderOwnerServiceController(ServiceCatalogService serviceCatalogService) {
        this.serviceCatalogService = serviceCatalogService;
    }

    @GetMapping
    public ApiResponse<List<ServiceResponse>> listServices(Authentication authentication, @PathVariable UUID providerId) {
        return ApiResponse.success(serviceCatalogService.listProviderServices(authentication, providerId));
    }

    @PostMapping
    public ApiResponse<ServiceResponse> createService(
            Authentication authentication,
            @PathVariable UUID providerId,
            @Valid @RequestBody CreateServiceRequest request
    ) {
        return ApiResponse.success("Service created.", serviceCatalogService.createService(authentication, providerId, request));
    }

    @PatchMapping("/{serviceId}")
    public ApiResponse<ServiceResponse> updateService(
            Authentication authentication,
            @PathVariable UUID providerId,
            @PathVariable UUID serviceId,
            @RequestBody UpdateServiceRequest request
    ) {
        return ApiResponse.success(serviceCatalogService.updateService(authentication, providerId, serviceId, request));
    }
}

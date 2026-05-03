package com.lebanonplatform.modules.serviceproviders.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.serviceproviders.application.ServiceProviderService;
import com.lebanonplatform.modules.serviceproviders.dto.response.ServiceProviderResponse;
import com.lebanonplatform.modules.services.application.ServiceCatalogService;
import com.lebanonplatform.modules.services.dto.response.ServiceResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/service-providers")
public class PublicServiceProviderController {

    private final ServiceProviderService serviceProviderService;
    private final ServiceCatalogService serviceCatalogService;

    public PublicServiceProviderController(ServiceProviderService serviceProviderService, ServiceCatalogService serviceCatalogService) {
        this.serviceProviderService = serviceProviderService;
        this.serviceCatalogService = serviceCatalogService;
    }

    @GetMapping
    public ApiResponse<List<ServiceProviderResponse>> listProviders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(serviceProviderService.listPublicProviders(page, size));
    }

    @GetMapping("/{providerId}")
    public ApiResponse<ServiceProviderResponse> getProvider(@PathVariable UUID providerId) {
        return ApiResponse.success(serviceProviderService.getPublicProvider(providerId));
    }

    @GetMapping("/{providerId}/services")
    public ApiResponse<List<ServiceResponse>> listServices(
            @PathVariable UUID providerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(serviceCatalogService.listPublicServices(providerId, page, size));
    }
}

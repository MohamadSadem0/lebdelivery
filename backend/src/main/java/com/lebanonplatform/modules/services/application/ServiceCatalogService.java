package com.lebanonplatform.modules.services.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.serviceproviders.application.ServiceProviderService;
import com.lebanonplatform.modules.serviceproviders.domain.ServiceProvider;
import com.lebanonplatform.modules.services.domain.PricingType;
import com.lebanonplatform.modules.services.dto.request.CreateServiceRequest;
import com.lebanonplatform.modules.services.dto.request.UpdateServiceRequest;
import com.lebanonplatform.modules.services.dto.response.ServiceResponse;
import com.lebanonplatform.modules.services.repository.ServiceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;
    private final ServiceProviderService serviceProviderService;

    public ServiceCatalogService(ServiceRepository serviceRepository, ServiceProviderService serviceProviderService) {
        this.serviceRepository = serviceRepository;
        this.serviceProviderService = serviceProviderService;
    }

    @Transactional
    public ServiceResponse createService(Authentication authentication, UUID providerId, CreateServiceRequest request) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        ServiceProvider provider = serviceProviderService.findProvider(providerId);
        com.lebanonplatform.modules.services.domain.Service service = new com.lebanonplatform.modules.services.domain.Service();
        service.setServiceProvider(provider);
        service.setName(request.name());
        service.setDescription(request.description());
        service.setBasePrice(request.basePrice());
        service.setPricingType(request.pricingType() == null ? PricingType.FIXED : request.pricingType());
        service.setActive(request.active() == null || request.active());
        return toResponse(serviceRepository.save(service));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> listProviderServices(Authentication authentication, UUID providerId) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        return serviceRepository.findByServiceProvider_IdOrderByCreatedAtDesc(providerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ServiceResponse updateService(Authentication authentication, UUID providerId, UUID serviceId, UpdateServiceRequest request) {
        serviceProviderService.ensureCanManageProvider(authentication, providerId);
        com.lebanonplatform.modules.services.domain.Service service = findService(serviceId);
        if (!service.getServiceProvider().getId().equals(providerId)) {
            throw new BaseApplicationException("SERVICE_DOES_NOT_BELONG_TO_PROVIDER", "Service does not belong to this provider.");
        }
        if (request.name() != null) {
            service.setName(request.name());
        }
        if (request.description() != null) {
            service.setDescription(request.description());
        }
        if (request.basePrice() != null) {
            service.setBasePrice(request.basePrice());
        }
        if (request.pricingType() != null) {
            service.setPricingType(request.pricingType());
        }
        if (request.active() != null) {
            service.setActive(request.active());
        }
        return toResponse(serviceRepository.save(service));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> listPublicServices(UUID providerId, int page, int size) {
        ServiceProvider provider = serviceProviderService.findProvider(providerId);
        if (!provider.isActive()) {
            throw new BaseApplicationException("PROVIDER_NOT_FOUND", "Service provider was not found.");
        }
        return serviceRepository.findByServiceProvider_IdAndActiveTrueOrderByCreatedAtDesc(providerId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toResponse)
                .toList();
    }

    public com.lebanonplatform.modules.services.domain.Service findService(UUID serviceId) {
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> new BaseApplicationException("SERVICE_NOT_FOUND", "Service was not found."));
    }

    public ServiceResponse toResponse(com.lebanonplatform.modules.services.domain.Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getServiceProvider().getId(),
                service.getServiceProvider().getName(),
                service.getName(),
                service.getDescription(),
                service.getBasePrice(),
                service.getPricingType(),
                service.isActive(),
                service.getCreatedAt(),
                service.getUpdatedAt()
        );
    }
}

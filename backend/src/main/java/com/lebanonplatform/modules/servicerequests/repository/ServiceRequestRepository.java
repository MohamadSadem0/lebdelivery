package com.lebanonplatform.modules.servicerequests.repository;

import com.lebanonplatform.modules.servicerequests.domain.ServiceRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, UUID> {

    Page<ServiceRequest> findByClient_IdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

    Page<ServiceRequest> findByServiceProvider_IdOrderByCreatedAtDesc(UUID serviceProviderId, Pageable pageable);

    Optional<ServiceRequest> findByIdAndClient_Id(UUID id, UUID clientId);

    Optional<ServiceRequest> findByIdAndServiceProvider_Id(UUID id, UUID serviceProviderId);
}

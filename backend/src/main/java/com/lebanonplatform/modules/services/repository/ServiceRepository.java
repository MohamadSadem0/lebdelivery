package com.lebanonplatform.modules.services.repository;

import com.lebanonplatform.modules.services.domain.Service;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, UUID> {

    Page<Service> findByServiceProvider_IdAndActiveTrueOrderByCreatedAtDesc(UUID serviceProviderId, Pageable pageable);

    List<Service> findByServiceProvider_IdOrderByCreatedAtDesc(UUID serviceProviderId);
}

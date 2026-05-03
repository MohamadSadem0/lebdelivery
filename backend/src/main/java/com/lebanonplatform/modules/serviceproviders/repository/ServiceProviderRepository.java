package com.lebanonplatform.modules.serviceproviders.repository;

import com.lebanonplatform.modules.serviceproviders.domain.ServiceProvider;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, UUID> {

    List<ServiceProvider> findByOwner_IdOrderByCreatedAtDesc(UUID ownerId);

    Page<ServiceProvider> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);
}

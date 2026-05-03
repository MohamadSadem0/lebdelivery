package com.lebanonplatform.modules.clients.repository;

import com.lebanonplatform.modules.clients.domain.ClientAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAddressRepository extends JpaRepository<ClientAddress, UUID> {

    List<ClientAddress> findByUser_IdAndActiveTrueOrderByCreatedAtDesc(UUID userId);

    Optional<ClientAddress> findByIdAndUser_IdAndActiveTrue(UUID id, UUID userId);

    boolean existsByUser_IdAndActiveTrue(UUID userId);
}

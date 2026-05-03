package com.lebanonplatform.modules.drivers.repository;

import com.lebanonplatform.modules.drivers.domain.Driver;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Optional<Driver> findByUser_Id(UUID userId);

    boolean existsByUser_Id(UUID userId);
}

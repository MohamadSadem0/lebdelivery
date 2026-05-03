package com.lebanonplatform.modules.inventory.repository;

import com.lebanonplatform.modules.inventory.domain.InventoryMovement;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {

    List<InventoryMovement> findByStoreIdOrderByCreatedAtDesc(UUID storeId);
}

package com.lebanonplatform.modules.inventory.application;

import com.lebanonplatform.modules.inventory.domain.InventoryMovement;
import com.lebanonplatform.modules.inventory.dto.response.InventoryMovementResponse;
import com.lebanonplatform.modules.inventory.repository.InventoryMovementRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class InventoryMovementService {

    private final InventoryMovementRepository inventoryMovementRepository;

    public InventoryMovementService(InventoryMovementRepository inventoryMovementRepository) {
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    public InventoryMovement save(InventoryMovement movement) {
        return inventoryMovementRepository.save(movement);
    }

    public List<InventoryMovementResponse> listForStore(UUID storeId) {
        return inventoryMovementRepository.findByStoreIdOrderByCreatedAtDesc(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    public InventoryMovementResponse toResponse(InventoryMovement movement) {
        return new InventoryMovementResponse(
                movement.getId(),
                movement.getStore().getId(),
                movement.getProduct().getId(),
                movement.getProductVariant() == null ? null : movement.getProductVariant().getId(),
                movement.getMovementType(),
                movement.getQuantityChange(),
                movement.getPreviousQuantity(),
                movement.getNewQuantity(),
                movement.getReason(),
                movement.getReferenceType(),
                movement.getReferenceId(),
                movement.getCreatedByUser() == null ? null : movement.getCreatedByUser().getId(),
                movement.getCreatedAt()
        );
    }
}

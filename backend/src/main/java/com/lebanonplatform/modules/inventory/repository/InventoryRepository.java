package com.lebanonplatform.modules.inventory.repository;

import com.lebanonplatform.modules.inventory.domain.Inventory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    List<Inventory> findByStoreIdOrderByUpdatedAtDesc(UUID storeId);

    List<Inventory> findByStoreIdAndQuantityLessThanEqualOrderByUpdatedAtDesc(UUID storeId, int quantity);

    Optional<Inventory> findByProductIdAndProductVariantIsNull(UUID productId);

    Optional<Inventory> findByProductIdAndProductVariantId(UUID productId, UUID productVariantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select inventory from Inventory inventory
            where inventory.product.id = :productId
              and inventory.productVariant is null
            """)
    Optional<Inventory> lockByProductIdAndNoVariant(@Param("productId") UUID productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select inventory from Inventory inventory
            where inventory.product.id = :productId
              and inventory.productVariant.id = :productVariantId
            """)
    Optional<Inventory> lockByProductIdAndProductVariantId(@Param("productId") UUID productId, @Param("productVariantId") UUID productVariantId);
}

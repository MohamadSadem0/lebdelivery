package com.lebanonplatform.modules.products.repository;

import com.lebanonplatform.modules.products.domain.ProductVariant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    List<ProductVariant> findByProductIdOrderByNameAsc(UUID productId);
}

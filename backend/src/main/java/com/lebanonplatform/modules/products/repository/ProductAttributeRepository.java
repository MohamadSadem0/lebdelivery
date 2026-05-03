package com.lebanonplatform.modules.products.repository;

import com.lebanonplatform.modules.products.domain.ProductAttribute;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, UUID> {

    List<ProductAttribute> findByProductIdOrderByKeyAsc(UUID productId);

    void deleteByProductId(UUID productId);
}

package com.lebanonplatform.modules.products.repository;

import com.lebanonplatform.modules.products.domain.Product;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByStoreIdOrderByCreatedAtDesc(UUID storeId, Pageable pageable);

    Page<Product> findByStoreIdAndActiveTrueAndAvailableTrueOrderByCreatedAtDesc(UUID storeId, Pageable pageable);
}

package com.lebanonplatform.modules.orders.repository;

import com.lebanonplatform.modules.orders.domain.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByClient_IdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

    Page<Order> findByStore_IdOrderByCreatedAtDesc(UUID storeId, Pageable pageable);

    Optional<Order> findByIdAndClient_Id(UUID id, UUID clientId);

    Optional<Order> findByIdAndStore_Id(UUID id, UUID storeId);
}

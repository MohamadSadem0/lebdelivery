package com.lebanonplatform.modules.stores.repository;

import com.lebanonplatform.modules.stores.domain.Store;
import com.lebanonplatform.modules.stores.domain.StoreStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    List<Store> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);

    Page<Store> findByStatusOrderByCreatedAtDesc(StoreStatus status, Pageable pageable);
}

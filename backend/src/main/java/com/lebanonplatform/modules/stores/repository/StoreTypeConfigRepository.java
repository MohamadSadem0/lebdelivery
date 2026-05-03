package com.lebanonplatform.modules.stores.repository;

import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import com.lebanonplatform.modules.stores.domain.StoreTypeConfig;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreTypeConfigRepository extends JpaRepository<StoreTypeConfig, UUID> {

    List<StoreTypeConfig> findAllByOrderByDisplayNameAsc();

    Optional<StoreTypeConfig> findByStoreTypeCode(StoreTypeCode storeTypeCode);
}

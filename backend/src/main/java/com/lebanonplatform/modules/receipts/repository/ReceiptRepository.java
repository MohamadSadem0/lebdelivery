package com.lebanonplatform.modules.receipts.repository;

import com.lebanonplatform.modules.receipts.domain.Receipt;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

    Optional<Receipt> findByOrder_Id(UUID orderId);

    Optional<Receipt> findByIdAndOrder_Client_Id(UUID id, UUID clientId);

    Optional<Receipt> findByIdAndOrder_Store_Id(UUID id, UUID storeId);

    List<Receipt> findByOrder_Client_IdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);

    List<Receipt> findByOrder_Store_IdOrderByCreatedAtDesc(UUID storeId, Pageable pageable);
}

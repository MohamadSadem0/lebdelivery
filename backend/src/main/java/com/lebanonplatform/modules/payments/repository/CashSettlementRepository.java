package com.lebanonplatform.modules.payments.repository;

import com.lebanonplatform.modules.payments.domain.CashSettlement;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashSettlementRepository extends JpaRepository<CashSettlement, UUID> {

    Page<CashSettlement> findByDriver_IdOrderByCreatedAtDesc(UUID driverId, Pageable pageable);

    @Query("select coalesce(sum(settlement.amount), 0) from CashSettlement settlement where settlement.driver.id = :driverId")
    BigDecimal sumAmountByDriver(@Param("driverId") UUID driverId);
}

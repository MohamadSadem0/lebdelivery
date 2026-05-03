package com.lebanonplatform.modules.payments.repository;

import com.lebanonplatform.modules.payments.domain.Payment;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrder_Id(UUID orderId);

    Optional<Payment> findByDelivery_Id(UUID deliveryId);

    @Query("select coalesce(sum(payment.collectedAmount), 0) from Payment payment where payment.collectedByDriver.id = :driverId")
    BigDecimal sumCollectedAmountByDriver(@Param("driverId") UUID driverId);
}

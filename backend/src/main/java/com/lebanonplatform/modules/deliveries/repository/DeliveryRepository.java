package com.lebanonplatform.modules.deliveries.repository;

import com.lebanonplatform.modules.deliveries.domain.Delivery;
import com.lebanonplatform.modules.deliveries.domain.DeliveryStatus;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    boolean existsByOrder_Id(UUID orderId);

    Optional<Delivery> findByOrder_Id(UUID orderId);

    Optional<Delivery> findByIdAndOrder_Store_Id(UUID deliveryId, UUID storeId);

    Optional<Delivery> findByIdAndDriver_Id(UUID deliveryId, UUID driverId);

    Page<Delivery> findByOrder_Store_IdOrderByCreatedAtDesc(UUID storeId, Pageable pageable);

    Page<Delivery> findByStatusOrderByCreatedAtDesc(DeliveryStatus status, Pageable pageable);

    List<Delivery> findByDriver_IdAndStatusInOrderByUpdatedAtDesc(UUID driverId, Collection<DeliveryStatus> statuses);

    Page<Delivery> findByDriver_IdAndStatusInOrderByUpdatedAtDesc(UUID driverId, Collection<DeliveryStatus> statuses, Pageable pageable);

    long countByDriver_IdAndStatus(UUID driverId, DeliveryStatus status);

    @Query("select coalesce(sum(delivery.deliveryFee), 0) from Delivery delivery where delivery.driver.id = :driverId and delivery.status = :status")
    java.math.BigDecimal sumDeliveryFeeByDriverAndStatus(@Param("driverId") UUID driverId, @Param("status") DeliveryStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select delivery from Delivery delivery where delivery.id = :deliveryId")
    Optional<Delivery> findByIdForUpdate(@Param("deliveryId") UUID deliveryId);
}

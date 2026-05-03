package com.lebanonplatform.modules.orders.repository;

import com.lebanonplatform.modules.orders.domain.OrderTimelineEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderTimelineEventRepository extends JpaRepository<OrderTimelineEvent, UUID> {

    List<OrderTimelineEvent> findByOrder_IdOrderByCreatedAtAsc(UUID orderId);
}

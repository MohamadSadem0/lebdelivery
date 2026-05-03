package com.lebanonplatform.modules.orders.application;

import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import com.lebanonplatform.modules.orders.domain.OrderTimelineEvent;
import com.lebanonplatform.modules.orders.dto.response.OrderTimelineEventResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderTimelineResponse;
import com.lebanonplatform.modules.orders.repository.OrderTimelineEventRepository;
import com.lebanonplatform.modules.users.domain.User;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OrderTimelineService {

    private final OrderTimelineEventRepository orderTimelineEventRepository;

    public OrderTimelineService(OrderTimelineEventRepository orderTimelineEventRepository) {
        this.orderTimelineEventRepository = orderTimelineEventRepository;
    }

    public void addEvent(Order order, OrderStatus status, String title, String description, User createdByUser) {
        OrderTimelineEvent event = new OrderTimelineEvent();
        event.setOrder(order);
        event.setStatus(status);
        event.setTitle(title);
        event.setDescription(description);
        event.setCreatedByUser(createdByUser);
        orderTimelineEventRepository.save(event);
    }

    public OrderTimelineResponse getTimeline(UUID orderId) {
        return new OrderTimelineResponse(orderId, listEvents(orderId));
    }

    public List<OrderTimelineEventResponse> listEvents(UUID orderId) {
        return orderTimelineEventRepository.findByOrder_IdOrderByCreatedAtAsc(orderId).stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderTimelineEventResponse toResponse(OrderTimelineEvent event) {
        return new OrderTimelineEventResponse(
                event.getId(),
                event.getStatus(),
                event.getTitle(),
                event.getDescription(),
                event.getCreatedByUser() == null ? null : event.getCreatedByUser().getId(),
                event.getCreatedAt()
        );
    }
}

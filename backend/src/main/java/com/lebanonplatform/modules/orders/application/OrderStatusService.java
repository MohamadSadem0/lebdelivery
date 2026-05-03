package com.lebanonplatform.modules.orders.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusService {

    public void requireTransition(OrderStatus current, OrderStatus next) {
        boolean allowed = switch (current) {
            case PENDING -> next == OrderStatus.ACCEPTED_BY_STORE
                    || next == OrderStatus.REJECTED_BY_STORE
                    || next == OrderStatus.CANCELLED;
            case ACCEPTED_BY_STORE -> next == OrderStatus.PREPARING;
            case PREPARING -> next == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> next == OrderStatus.DRIVER_ASSIGNED;
            case DRIVER_ASSIGNED -> next == OrderStatus.PICKED_UP;
            case PICKED_UP -> next == OrderStatus.ON_THE_WAY;
            case ON_THE_WAY -> next == OrderStatus.DELIVERED;
            case DELIVERED -> next == OrderStatus.COMPLETED;
            default -> false;
        };

        if (!allowed) {
            throw new BaseApplicationException(
                    "INVALID_ORDER_STATUS_TRANSITION",
                    "Order cannot transition from " + current + " to " + next + "."
            );
        }
    }
}

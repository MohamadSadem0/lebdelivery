package com.lebanonplatform.modules.deliveries.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.deliveries.domain.DeliveryStatus;
import org.springframework.stereotype.Service;

@Service
public class DeliveryStatusService {

    public void requireTransition(DeliveryStatus current, DeliveryStatus next) {
        boolean allowed = switch (current) {
            case WAITING_FOR_DRIVER -> next == DeliveryStatus.DRIVER_ASSIGNED || next == DeliveryStatus.CANCELLED;
            case DRIVER_ASSIGNED -> next == DeliveryStatus.ARRIVED_AT_PICKUP || next == DeliveryStatus.CANCELLED;
            case ARRIVED_AT_PICKUP -> next == DeliveryStatus.PICKED_UP || next == DeliveryStatus.CANCELLED;
            case PICKED_UP -> next == DeliveryStatus.ON_THE_WAY || next == DeliveryStatus.CANCELLED;
            case ON_THE_WAY -> next == DeliveryStatus.ARRIVED_AT_DESTINATION || next == DeliveryStatus.FAILED_DELIVERY;
            case ARRIVED_AT_DESTINATION -> next == DeliveryStatus.DELIVERED || next == DeliveryStatus.FAILED_DELIVERY;
            default -> false;
        };

        if (!allowed) {
            throw new BaseApplicationException(
                    "INVALID_DELIVERY_STATUS_TRANSITION",
                    "Delivery cannot transition from " + current + " to " + next + "."
            );
        }
    }
}

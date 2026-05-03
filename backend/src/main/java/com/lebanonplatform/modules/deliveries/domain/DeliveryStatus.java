package com.lebanonplatform.modules.deliveries.domain;

public enum DeliveryStatus {
    WAITING_FOR_DRIVER,
    DRIVER_ASSIGNED,
    ARRIVED_AT_PICKUP,
    PICKED_UP,
    ON_THE_WAY,
    ARRIVED_AT_DESTINATION,
    DELIVERED,
    FAILED_DELIVERY,
    CANCELLED,
    ISSUE_REPORTED
}

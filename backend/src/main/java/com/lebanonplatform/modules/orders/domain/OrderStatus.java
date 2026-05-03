package com.lebanonplatform.modules.orders.domain;

public enum OrderStatus {
    PENDING,
    ACCEPTED_BY_STORE,
    REJECTED_BY_STORE,
    PREPARING,
    READY_FOR_PICKUP,
    DRIVER_ASSIGNED,
    PICKED_UP,
    ON_THE_WAY,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    ISSUE_REPORTED
}

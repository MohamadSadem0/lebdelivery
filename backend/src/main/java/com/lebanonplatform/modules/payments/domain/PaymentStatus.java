package com.lebanonplatform.modules.payments.domain;

public enum PaymentStatus {
    UNPAID,
    PENDING_COLLECTION,
    COLLECTED,
    PAID,
    DISPUTED,
    REFUNDED,
    CANCELLED
}

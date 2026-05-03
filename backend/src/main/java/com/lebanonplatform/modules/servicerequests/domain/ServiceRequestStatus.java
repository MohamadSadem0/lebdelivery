package com.lebanonplatform.modules.servicerequests.domain;

public enum ServiceRequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    WAITING_FOR_QUOTE,
    QUOTE_SENT,
    QUOTE_ACCEPTED,
    IN_PROGRESS,
    READY,
    COMPLETED,
    CANCELLED,
    ISSUE_REPORTED
}

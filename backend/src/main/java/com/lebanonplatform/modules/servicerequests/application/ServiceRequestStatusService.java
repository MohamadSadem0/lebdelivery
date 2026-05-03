package com.lebanonplatform.modules.servicerequests.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.servicerequests.domain.ServiceRequestStatus;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ServiceRequestStatusService {

    private static final Map<ServiceRequestStatus, Set<ServiceRequestStatus>> ALLOWED = Map.of(
            ServiceRequestStatus.PENDING, Set.of(ServiceRequestStatus.ACCEPTED, ServiceRequestStatus.REJECTED, ServiceRequestStatus.CANCELLED, ServiceRequestStatus.WAITING_FOR_QUOTE),
            ServiceRequestStatus.WAITING_FOR_QUOTE, Set.of(ServiceRequestStatus.ACCEPTED, ServiceRequestStatus.QUOTE_SENT, ServiceRequestStatus.REJECTED, ServiceRequestStatus.CANCELLED),
            ServiceRequestStatus.ACCEPTED, Set.of(ServiceRequestStatus.IN_PROGRESS, ServiceRequestStatus.CANCELLED),
            ServiceRequestStatus.QUOTE_SENT, Set.of(ServiceRequestStatus.QUOTE_ACCEPTED, ServiceRequestStatus.REJECTED, ServiceRequestStatus.CANCELLED),
            ServiceRequestStatus.QUOTE_ACCEPTED, Set.of(ServiceRequestStatus.IN_PROGRESS, ServiceRequestStatus.CANCELLED),
            ServiceRequestStatus.IN_PROGRESS, Set.of(ServiceRequestStatus.READY, ServiceRequestStatus.COMPLETED, ServiceRequestStatus.ISSUE_REPORTED),
            ServiceRequestStatus.READY, Set.of(ServiceRequestStatus.COMPLETED, ServiceRequestStatus.ISSUE_REPORTED)
    );

    public void requireTransition(ServiceRequestStatus current, ServiceRequestStatus next) {
        if (!ALLOWED.getOrDefault(current, Set.of()).contains(next)) {
            throw new BaseApplicationException("INVALID_SERVICE_REQUEST_STATUS_TRANSITION", "Cannot move service request from " + current + " to " + next + ".");
        }
    }
}

package com.lebanonplatform.modules.payments.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentStatusService {

    public void requireTransition(PaymentStatus current, PaymentStatus next) {
        boolean allowed = switch (current) {
            case UNPAID -> next == PaymentStatus.PENDING_COLLECTION || next == PaymentStatus.CANCELLED;
            case PENDING_COLLECTION -> next == PaymentStatus.COLLECTED || next == PaymentStatus.CANCELLED;
            case COLLECTED -> next == PaymentStatus.PAID || next == PaymentStatus.DISPUTED;
            case PAID -> next == PaymentStatus.REFUNDED;
            default -> false;
        };

        if (!allowed) {
            throw new BaseApplicationException(
                    "INVALID_PAYMENT_STATUS_TRANSITION",
                    "Payment cannot transition from " + current + " to " + next + "."
            );
        }
    }
}

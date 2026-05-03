package com.lebanonplatform.modules.payments;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.payments.application.PaymentStatusService;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentStatusServiceTest {

    private final PaymentStatusService service = new PaymentStatusService();

    @Test
    void allowsCodCollectionProgression() {
        assertThatCode(() -> service.requireTransition(PaymentStatus.PENDING_COLLECTION, PaymentStatus.COLLECTED))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(PaymentStatus.COLLECTED, PaymentStatus.PAID))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsSkippingCollection() {
        assertThatThrownBy(() -> service.requireTransition(PaymentStatus.PENDING_COLLECTION, PaymentStatus.PAID))
                .isInstanceOf(BaseApplicationException.class)
                .extracting("code")
                .isEqualTo("INVALID_PAYMENT_STATUS_TRANSITION");
    }

    @Test
    void rejectsChangingCancelledPayment() {
        assertThatThrownBy(() -> service.requireTransition(PaymentStatus.CANCELLED, PaymentStatus.COLLECTED))
                .isInstanceOf(BaseApplicationException.class)
                .extracting("code")
                .isEqualTo("INVALID_PAYMENT_STATUS_TRANSITION");
    }
}

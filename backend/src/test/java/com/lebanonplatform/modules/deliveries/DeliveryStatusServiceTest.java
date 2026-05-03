package com.lebanonplatform.modules.deliveries;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.deliveries.application.DeliveryStatusService;
import com.lebanonplatform.modules.deliveries.domain.DeliveryStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class DeliveryStatusServiceTest {

    private final DeliveryStatusService service = new DeliveryStatusService();

    @Test
    void allowsDriverDeliveryProgression() {
        assertThatCode(() -> service.requireTransition(DeliveryStatus.WAITING_FOR_DRIVER, DeliveryStatus.DRIVER_ASSIGNED))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(DeliveryStatus.DRIVER_ASSIGNED, DeliveryStatus.ARRIVED_AT_PICKUP))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(DeliveryStatus.ARRIVED_AT_PICKUP, DeliveryStatus.PICKED_UP))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(DeliveryStatus.PICKED_UP, DeliveryStatus.ON_THE_WAY))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(DeliveryStatus.ON_THE_WAY, DeliveryStatus.ARRIVED_AT_DESTINATION))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(DeliveryStatus.ARRIVED_AT_DESTINATION, DeliveryStatus.DELIVERED))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsSkippingPickupSteps() {
        assertThatThrownBy(() -> service.requireTransition(DeliveryStatus.WAITING_FOR_DRIVER, DeliveryStatus.PICKED_UP))
                .isInstanceOf(BaseApplicationException.class)
                .hasMessageContaining("WAITING_FOR_DRIVER")
                .hasMessageContaining("PICKED_UP")
                .extracting("code")
                .isEqualTo("INVALID_DELIVERY_STATUS_TRANSITION");
    }

    @Test
    void rejectsChangingCompletedDelivery() {
        assertThatThrownBy(() -> service.requireTransition(DeliveryStatus.DELIVERED, DeliveryStatus.ON_THE_WAY))
                .isInstanceOf(BaseApplicationException.class)
                .extracting("code")
                .isEqualTo("INVALID_DELIVERY_STATUS_TRANSITION");
    }

    @Test
    void allowsFailureOnlyNearDestinationForMvp() {
        assertThatCode(() -> service.requireTransition(DeliveryStatus.ON_THE_WAY, DeliveryStatus.FAILED_DELIVERY))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(DeliveryStatus.ARRIVED_AT_DESTINATION, DeliveryStatus.FAILED_DELIVERY))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> service.requireTransition(DeliveryStatus.WAITING_FOR_DRIVER, DeliveryStatus.FAILED_DELIVERY))
                .isInstanceOf(BaseApplicationException.class)
                .extracting("code")
                .isEqualTo("INVALID_DELIVERY_STATUS_TRANSITION");
    }
}

package com.lebanonplatform.modules.orders;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.orders.application.OrderStatusService;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderStatusServiceTest {

    private final OrderStatusService service = new OrderStatusService();

    @Test
    void allowsStoreAndDeliveryOrderProgression() {
        assertThatCode(() -> service.requireTransition(OrderStatus.PENDING, OrderStatus.ACCEPTED_BY_STORE))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.ACCEPTED_BY_STORE, OrderStatus.PREPARING))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.READY_FOR_PICKUP, OrderStatus.DRIVER_ASSIGNED))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.DRIVER_ASSIGNED, OrderStatus.PICKED_UP))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.PICKED_UP, OrderStatus.ON_THE_WAY))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.ON_THE_WAY, OrderStatus.DELIVERED))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.DELIVERED, OrderStatus.COMPLETED))
                .doesNotThrowAnyException();
    }

    @Test
    void allowsPendingOrderTerminalStoreDecisions() {
        assertThatCode(() -> service.requireTransition(OrderStatus.PENDING, OrderStatus.REJECTED_BY_STORE))
                .doesNotThrowAnyException();
        assertThatCode(() -> service.requireTransition(OrderStatus.PENDING, OrderStatus.CANCELLED))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsSkippingStorePreparation() {
        assertThatThrownBy(() -> service.requireTransition(OrderStatus.PENDING, OrderStatus.READY_FOR_PICKUP))
                .isInstanceOf(BaseApplicationException.class)
                .hasMessageContaining("PENDING")
                .hasMessageContaining("READY_FOR_PICKUP")
                .extracting("code")
                .isEqualTo("INVALID_ORDER_STATUS_TRANSITION");
    }

    @Test
    void rejectsMovingBackwardsAfterPickup() {
        assertThatThrownBy(() -> service.requireTransition(OrderStatus.PICKED_UP, OrderStatus.PREPARING))
                .isInstanceOf(BaseApplicationException.class)
                .extracting("code")
                .isEqualTo("INVALID_ORDER_STATUS_TRANSITION");
    }
}

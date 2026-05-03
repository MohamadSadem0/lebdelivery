package com.lebanonplatform.modules.orders;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Enable when Java 21/Maven test environment and test database are available.")
class OrderMvpTodoTests {

    @Test
    void clientCanCreateOrderAndBackendCalculatesSubtotal() {
    }

    @Test
    void clientCannotOrderUnavailableProductOrMoreThanAvailableStock() {
    }

    @Test
    void storeOwnerCanSeeOwnStoreOrderButNotAnotherStoreOrder() {
    }

    @Test
    void acceptingOrderReducesInventoryAndCreatesMovementAndTimelineEvent() {
    }

    @Test
    void invalidOrderStatusTransitionFails() {
    }

    @Test
    void clientCanOnlyCancelPendingOrder() {
    }
}

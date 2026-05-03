package com.lebanonplatform.modules.stores;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Enable when Java 21/Maven test environment and test database are available.")
class StoreProductInventoryTodoTests {

    @Test
    void authenticatedUserCanCreateStoreAndReceivesStoreOwnerRole() {
    }

    @Test
    void userCannotManageStoreTheyDoNotOwn() {
    }

    @Test
    void publicStoreListOnlyReturnsActiveStores() {
    }

    @Test
    void storeOwnerCanCreateProductWithInitialInventory() {
    }

    @Test
    void inventoryAdjustmentCreatesMovementAndCannotGoBelowZero() {
    }

    @Test
    void lowStockStatusUpdatesAfterAdjustment() {
    }
}

package com.lebanonplatform.modules.inventory.domain;

public enum InventoryMovementType {
    STOCK_ADDED,
    STOCK_REMOVED,
    ORDER_SALE,
    ORDER_CANCELLED,
    MANUAL_ADJUSTMENT,
    DAMAGED,
    RETURNED,
    EXPIRED
}

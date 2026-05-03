package com.lebanonplatform.modules.orders.dto.request;

import com.lebanonplatform.modules.orders.domain.FulfillmentType;
import com.lebanonplatform.modules.payments.domain.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID storeId,
        @NotNull PaymentMethod paymentMethod,
        @NotNull FulfillmentType fulfillmentType,
        Instant scheduledFor,
        boolean needsChange,
        BigDecimal cashAmountClientHas,
        String notes,
        UUID addressId,
        @Valid OrderAddressRequest address,
        @NotEmpty @Valid List<CreateOrderItemRequest> items
) {
}

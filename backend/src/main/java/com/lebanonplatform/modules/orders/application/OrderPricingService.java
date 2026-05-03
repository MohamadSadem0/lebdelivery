package com.lebanonplatform.modules.orders.application;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class OrderPricingService {

    public BigDecimal deliveryFee() {
        // TODO: calculate delivery fee from distance, store settings, and delivery mode.
        return BigDecimal.ZERO;
    }

    public BigDecimal discount() {
        return BigDecimal.ZERO;
    }

    public BigDecimal total(BigDecimal subtotal, BigDecimal deliveryFee, BigDecimal discount) {
        return subtotal.add(deliveryFee).subtract(discount);
    }
}

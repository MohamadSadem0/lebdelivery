package com.lebanonplatform.modules.payments.domain;

import com.lebanonplatform.common.audit.BaseEntity;
import com.lebanonplatform.modules.deliveries.domain.Delivery;
import com.lebanonplatform.modules.drivers.domain.Driver;
import com.lebanonplatform.modules.orders.domain.Order;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PaymentStatus status = PaymentStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PaymentMethod method = PaymentMethod.CASH_ON_DELIVERY;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_by_driver_id")
    private Driver collectedByDriver;

    @Column(precision = 12, scale = 2)
    private BigDecimal collectedAmount;

    @Column
    private Instant collectedAt;

    @Column(nullable = false)
    private boolean cashMismatch;

    @Column(columnDefinition = "TEXT")
    private String collectionNote;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Driver getCollectedByDriver() {
        return collectedByDriver;
    }

    public void setCollectedByDriver(Driver collectedByDriver) {
        this.collectedByDriver = collectedByDriver;
    }

    public BigDecimal getCollectedAmount() {
        return collectedAmount;
    }

    public void setCollectedAmount(BigDecimal collectedAmount) {
        this.collectedAmount = collectedAmount;
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(Instant collectedAt) {
        this.collectedAt = collectedAt;
    }

    public boolean isCashMismatch() {
        return cashMismatch;
    }

    public void setCashMismatch(boolean cashMismatch) {
        this.cashMismatch = cashMismatch;
    }

    public String getCollectionNote() {
        return collectionNote;
    }

    public void setCollectionNote(String collectionNote) {
        this.collectionNote = collectionNote;
    }
}

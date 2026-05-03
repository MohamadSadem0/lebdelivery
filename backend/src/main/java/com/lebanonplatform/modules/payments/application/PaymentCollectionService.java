package com.lebanonplatform.modules.payments.application;

import com.lebanonplatform.common.audit.AuditService;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.deliveries.domain.Delivery;
import com.lebanonplatform.modules.deliveries.domain.DeliveryStatus;
import com.lebanonplatform.modules.deliveries.repository.DeliveryRepository;
import com.lebanonplatform.modules.drivers.application.DriverService;
import com.lebanonplatform.modules.drivers.domain.Driver;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.orders.application.OrderStatusService;
import com.lebanonplatform.modules.orders.application.OrderTimelineService;
import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import com.lebanonplatform.modules.orders.repository.OrderRepository;
import com.lebanonplatform.modules.payments.domain.Payment;
import com.lebanonplatform.modules.payments.domain.PaymentMethod;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import com.lebanonplatform.modules.payments.dto.request.CashCollectionRequest;
import com.lebanonplatform.modules.payments.dto.response.CashCollectionResponse;
import com.lebanonplatform.modules.payments.dto.response.PaymentResponse;
import com.lebanonplatform.modules.payments.repository.PaymentRepository;
import com.lebanonplatform.modules.receipts.application.ReceiptService;
import com.lebanonplatform.modules.receipts.dto.response.ReceiptResponse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentCollectionService {

    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final DriverService driverService;
    private final PaymentStatusService paymentStatusService;
    private final OrderStatusService orderStatusService;
    private final OrderTimelineService orderTimelineService;
    private final ReceiptService receiptService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public PaymentCollectionService(
            DeliveryRepository deliveryRepository,
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            DriverService driverService,
            PaymentStatusService paymentStatusService,
            OrderStatusService orderStatusService,
            OrderTimelineService orderTimelineService,
            ReceiptService receiptService,
            AuditService auditService,
            NotificationService notificationService
    ) {
        this.deliveryRepository = deliveryRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.driverService = driverService;
        this.paymentStatusService = paymentStatusService;
        this.orderStatusService = orderStatusService;
        this.orderTimelineService = orderTimelineService;
        this.receiptService = receiptService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @Transactional
    public CashCollectionResponse markCashCollected(Authentication authentication, UUID deliveryId, CashCollectionRequest request) {
        Driver driver = driverService.currentActiveDriver(authentication);
        Delivery delivery = deliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_NOT_FOUND", "Delivery was not found."));
        if (delivery.getDriver() == null || !delivery.getDriver().getId().equals(driver.getId())) {
            throw new BaseApplicationException("DELIVERY_ACCESS_DENIED", "Delivery does not belong to this driver.");
        }
        if (delivery.getStatus() != DeliveryStatus.DELIVERED) {
            throw new BaseApplicationException("DELIVERY_NOT_DELIVERED", "Cash can be collected only after delivery is marked delivered.");
        }

        Order order = delivery.getOrder();
        if (order.getPaymentMethod() != PaymentMethod.CASH_ON_DELIVERY) {
            throw new BaseApplicationException("PAYMENT_METHOD_NOT_COD", "Cash collection is only available for cash-on-delivery orders.");
        }
        if (order.getPaymentStatus() == PaymentStatus.COLLECTED || order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BaseApplicationException("PAYMENT_ALREADY_COLLECTED", "Payment was already collected.");
        }

        Payment payment = paymentRepository.findByOrder_Id(order.getId()).orElse(null);
        if (payment == null) {
            payment = createPendingPayment(order, delivery);
        }
        paymentStatusService.requireTransition(payment.getStatus(), PaymentStatus.COLLECTED);
        payment.setStatus(PaymentStatus.COLLECTED);
        payment.setCollectedByDriver(driver);
        payment.setCollectedAmount(request.collectedAmount());
        payment.setCollectedAt(Instant.now());
        payment.setCashMismatch(request.collectedAmount().compareTo(order.getTotal()) != 0);
        payment.setCollectionNote(request.note());
        payment = paymentRepository.save(payment);

        paymentStatusService.requireTransition(order.getPaymentStatus(), PaymentStatus.COLLECTED);
        order.setPaymentStatus(PaymentStatus.COLLECTED);
        if (order.getStatus() == OrderStatus.DELIVERED) {
            orderStatusService.requireTransition(order.getStatus(), OrderStatus.COMPLETED);
            order.setStatus(OrderStatus.COMPLETED);
            orderTimelineService.addEvent(order, OrderStatus.COMPLETED, "Order completed", "Cash was collected and the order was completed.", driver.getUser());
        }
        order = orderRepository.save(order);

        ReceiptResponse receipt = receiptService.getOrCreateStoreOrderReceipt(order, payment);
        auditService.record(driver.getUser().getId(), "CASH_COLLECTED", "DELIVERY", delivery.getId(), "{}");
        notificationService.create(order.getClient().getId(), "Payment collected", "Cash was collected for order " + order.getOrderNumber() + ".");
        notificationService.create(order.getStore().getOwner() == null ? null : order.getStore().getOwner().getId(), "Cash collected", "Cash was collected for order " + order.getOrderNumber() + ".");
        return new CashCollectionResponse(toResponse(payment), receipt);
    }

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder() == null ? null : payment.getOrder().getId(),
                payment.getDelivery() == null ? null : payment.getDelivery().getId(),
                payment.getStatus(),
                payment.getMethod(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCollectedByDriver() == null ? null : payment.getCollectedByDriver().getId(),
                payment.getCollectedAmount(),
                payment.getCollectedAt(),
                payment.isCashMismatch(),
                payment.getCollectionNote()
        );
    }

    private Payment createPendingPayment(Order order, Delivery delivery) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setDelivery(delivery);
        payment.setStatus(PaymentStatus.PENDING_COLLECTION);
        payment.setMethod(order.getPaymentMethod());
        payment.setAmount(order.getTotal());
        payment.setCurrency("USD");
        return paymentRepository.save(payment);
    }
}

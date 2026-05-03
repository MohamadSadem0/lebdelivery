package com.lebanonplatform.modules.admin.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.deliveries.application.DeliveryService;
import com.lebanonplatform.modules.deliveries.dto.response.DeliveryResponse;
import com.lebanonplatform.modules.deliveries.dto.response.DeliverySummaryResponse;
import com.lebanonplatform.modules.deliveries.repository.DeliveryRepository;
import com.lebanonplatform.modules.orders.application.ClientOrderService;
import com.lebanonplatform.modules.orders.dto.response.OrderDetailsResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderSummaryResponse;
import com.lebanonplatform.modules.orders.repository.OrderRepository;
import com.lebanonplatform.modules.payments.application.PaymentCollectionService;
import com.lebanonplatform.modules.payments.application.CashSettlementService;
import com.lebanonplatform.modules.payments.dto.request.CreateCashSettlementRequest;
import com.lebanonplatform.modules.payments.dto.response.CashSettlementResponse;
import com.lebanonplatform.modules.payments.dto.response.PaymentResponse;
import com.lebanonplatform.modules.payments.repository.PaymentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminOperationsService {

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final ClientOrderService clientOrderService;
    private final DeliveryService deliveryService;
    private final PaymentCollectionService paymentCollectionService;
    private final CashSettlementService cashSettlementService;

    public AdminOperationsService(
            OrderRepository orderRepository,
            DeliveryRepository deliveryRepository,
            PaymentRepository paymentRepository,
            ClientOrderService clientOrderService,
            DeliveryService deliveryService,
            PaymentCollectionService paymentCollectionService,
            CashSettlementService cashSettlementService
    ) {
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
        this.paymentRepository = paymentRepository;
        this.clientOrderService = clientOrderService;
        this.deliveryService = deliveryService;
        this.paymentCollectionService = paymentCollectionService;
        this.cashSettlementService = cashSettlementService;
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> listOrders(int page, int size) {
        return orderRepository.findAll(pageRequest(page, size)).stream()
                .map(clientOrderService::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(clientOrderService::toDetails)
                .orElseThrow(() -> new BaseApplicationException("ORDER_NOT_FOUND", "Order was not found."));
    }

    @Transactional(readOnly = true)
    public List<DeliverySummaryResponse> listDeliveries(int page, int size) {
        return deliveryRepository.findAll(pageRequest(page, size)).stream()
                .map(deliveryService::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDelivery(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .map(deliveryService::toResponse)
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_NOT_FOUND", "Delivery was not found."));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments(int page, int size) {
        return paymentRepository.findAll(pageRequest(page, size)).stream()
                .map(paymentCollectionService::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .map(paymentCollectionService::toResponse)
                .orElseThrow(() -> new BaseApplicationException("PAYMENT_NOT_FOUND", "Payment was not found."));
    }

    @Transactional(readOnly = true)
    public List<CashSettlementResponse> listCashSettlements(UUID driverId, int page, int size) {
        return cashSettlementService.list(driverId, page, size);
    }

    @Transactional
    public CashSettlementResponse createCashSettlement(Authentication authentication, CreateCashSettlementRequest request) {
        return cashSettlementService.create(authentication, request);
    }

    private PageRequest pageRequest(int page, int size) {
        return PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}

package com.lebanonplatform.modules.deliveries.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.deliveries.domain.Delivery;
import com.lebanonplatform.modules.deliveries.domain.DeliveryStatus;
import com.lebanonplatform.modules.deliveries.dto.request.DeliveryFailureRequest;
import com.lebanonplatform.modules.deliveries.dto.request.RequestDeliveryRequest;
import com.lebanonplatform.modules.deliveries.dto.response.DeliveryResponse;
import com.lebanonplatform.modules.deliveries.dto.response.DeliverySummaryResponse;
import com.lebanonplatform.modules.deliveries.dto.response.DriverEarningsResponse;
import com.lebanonplatform.modules.deliveries.repository.DeliveryRepository;
import com.lebanonplatform.modules.drivers.application.DriverService;
import com.lebanonplatform.modules.drivers.domain.Driver;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.orders.application.OrderStatusService;
import com.lebanonplatform.modules.orders.application.OrderTimelineService;
import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import com.lebanonplatform.modules.orders.repository.OrderRepository;
import com.lebanonplatform.modules.payments.repository.PaymentRepository;
import com.lebanonplatform.modules.payments.application.CashSettlementService;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.stores.domain.DeliveryMode;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final StoreService storeService;
    private final DriverService driverService;
    private final DeliveryStatusService deliveryStatusService;
    private final OrderStatusService orderStatusService;
    private final OrderTimelineService orderTimelineService;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;
    private final CashSettlementService cashSettlementService;

    public DeliveryService(
            DeliveryRepository deliveryRepository,
            OrderRepository orderRepository,
            StoreService storeService,
            DriverService driverService,
            DeliveryStatusService deliveryStatusService,
            OrderStatusService orderStatusService,
            OrderTimelineService orderTimelineService,
            NotificationService notificationService,
            PaymentRepository paymentRepository,
            CashSettlementService cashSettlementService
    ) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
        this.storeService = storeService;
        this.driverService = driverService;
        this.deliveryStatusService = deliveryStatusService;
        this.orderStatusService = orderStatusService;
        this.orderTimelineService = orderTimelineService;
        this.notificationService = notificationService;
        this.paymentRepository = paymentRepository;
        this.cashSettlementService = cashSettlementService;
    }

    @Transactional
    public DeliveryResponse requestDelivery(Authentication authentication, UUID storeId, UUID orderId, RequestDeliveryRequest request) {
        storeService.ensureCanManageStore(authentication, storeId);
        Order order = orderRepository.findByIdAndStore_Id(orderId, storeId)
                .orElseThrow(() -> new BaseApplicationException("ORDER_DOES_NOT_BELONG_TO_STORE", "Order does not belong to this store."));
        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new BaseApplicationException("ORDER_NOT_READY_FOR_DELIVERY", "Order must be ready for pickup before requesting delivery.");
        }
        if (order.getStore().getDeliveryMode() == DeliveryMode.PICKUP_ONLY) {
            throw new BaseApplicationException("STORE_PICKUP_ONLY", "This store is configured for pickup only.");
        }
        if (deliveryRepository.existsByOrder_Id(order.getId())) {
            throw new BaseApplicationException("DELIVERY_ALREADY_REQUESTED", "A delivery has already been requested for this order.");
        }

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setStatus(DeliveryStatus.WAITING_FOR_DRIVER);
        delivery.setPickupAddressSnapshot(request != null && request.pickupAddress() != null ? request.pickupAddress() : order.getStore().getAddress());
        delivery.setDropoffAddressSnapshot(request != null && request.dropoffAddress() != null ? request.dropoffAddress() : order.getAddressSnapshot());
        delivery.setDeliveryFee(request != null && request.deliveryFee() != null ? request.deliveryFee() : order.getDeliveryFee());
        delivery = deliveryRepository.save(delivery);

        orderTimelineService.addEvent(order, OrderStatus.READY_FOR_PICKUP, "Delivery requested", "The order is waiting for a driver.", null);
        notificationService.create(order.getClient().getId(), "Delivery requested", "Order " + order.getOrderNumber() + " is waiting for a driver.");
        return toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliverySummaryResponse> listStoreDeliveries(Authentication authentication, UUID storeId, int page, int size) {
        storeService.ensureCanManageStore(authentication, storeId);
        return deliveryRepository.findByOrder_Store_IdOrderByCreatedAtDesc(storeId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getStoreDelivery(Authentication authentication, UUID storeId, UUID deliveryId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return toResponse(deliveryRepository.findByIdAndOrder_Store_Id(deliveryId, storeId)
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_NOT_FOUND", "Delivery was not found.")));
    }

    @Transactional(readOnly = true)
    public List<DeliverySummaryResponse> listAvailableJobs(Authentication authentication, int page, int size) {
        driverService.currentActiveDriver(authentication);
        return deliveryRepository.findByStatusOrderByCreatedAtDesc(DeliveryStatus.WAITING_FOR_DRIVER, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDriverJob(Authentication authentication, UUID deliveryId) {
        Driver driver = driverService.currentActiveDriver(authentication);
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_NOT_FOUND", "Delivery was not found."));
        if (delivery.getStatus() == DeliveryStatus.WAITING_FOR_DRIVER) {
            return toResponse(delivery);
        }
        if (delivery.getDriver() == null || !delivery.getDriver().getId().equals(driver.getId())) {
            throw new BaseApplicationException("DELIVERY_ACCESS_DENIED", "Delivery is not available to this driver.");
        }
        return toResponse(delivery);
    }

    @Transactional
    public DeliveryResponse acceptJob(Authentication authentication, UUID deliveryId) {
        Driver driver = driverService.currentActiveDriver(authentication);
        Delivery delivery = deliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_NOT_FOUND", "Delivery was not found."));
        deliveryStatusService.requireTransition(delivery.getStatus(), DeliveryStatus.DRIVER_ASSIGNED);
        if (delivery.getDriver() != null) {
            throw new BaseApplicationException("DELIVERY_ALREADY_ASSIGNED", "Delivery already has a driver.");
        }

        Order order = delivery.getOrder();
        orderStatusService.requireTransition(order.getStatus(), OrderStatus.DRIVER_ASSIGNED);
        delivery.setDriver(driver);
        delivery.setStatus(DeliveryStatus.DRIVER_ASSIGNED);
        delivery.setAcceptedAt(Instant.now());
        delivery = deliveryRepository.save(delivery);

        order.setStatus(OrderStatus.DRIVER_ASSIGNED);
        orderRepository.save(order);
        orderTimelineService.addEvent(order, OrderStatus.DRIVER_ASSIGNED, "Driver assigned", "A driver accepted the delivery.", driver.getUser());
        notificationService.create(order.getClient().getId(), "Driver assigned", "A driver accepted delivery for order " + order.getOrderNumber() + ".");
        notificationService.create(order.getStore().getOwner() == null ? null : order.getStore().getOwner().getId(), "Driver assigned", "A driver accepted delivery for order " + order.getOrderNumber() + ".");
        return toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliverySummaryResponse> listActiveDriverDeliveries(Authentication authentication) {
        Driver driver = driverService.currentActiveDriver(authentication);
        return deliveryRepository.findByDriver_IdAndStatusInOrderByUpdatedAtDesc(
                        driver.getId(),
                        List.of(DeliveryStatus.DRIVER_ASSIGNED, DeliveryStatus.ARRIVED_AT_PICKUP, DeliveryStatus.PICKED_UP, DeliveryStatus.ON_THE_WAY, DeliveryStatus.ARRIVED_AT_DESTINATION, DeliveryStatus.DELIVERED)
                ).stream()
                .filter(delivery -> delivery.getStatus() != DeliveryStatus.DELIVERED
                        || (delivery.getOrder() != null
                        && delivery.getOrder().getPaymentStatus() != PaymentStatus.COLLECTED
                        && delivery.getOrder().getPaymentStatus() != PaymentStatus.PAID))
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeliverySummaryResponse> listDriverHistory(Authentication authentication, int page, int size) {
        Driver driver = driverService.currentActiveDriver(authentication);
        return deliveryRepository.findByDriver_IdAndStatusInOrderByUpdatedAtDesc(
                        driver.getId(),
                        List.of(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED_DELIVERY, DeliveryStatus.CANCELLED),
                        PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))
                ).stream()
                .filter(delivery -> delivery.getStatus() != DeliveryStatus.DELIVERED
                        || (delivery.getOrder() == null
                        || delivery.getOrder().getPaymentStatus() == PaymentStatus.COLLECTED
                        || delivery.getOrder().getPaymentStatus() == PaymentStatus.PAID))
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public DriverEarningsResponse getDriverEarnings(Authentication authentication) {
        Driver driver = driverService.currentActiveDriver(authentication);
        long completedDeliveries = deliveryRepository.countByDriver_IdAndStatus(driver.getId(), DeliveryStatus.DELIVERED);
        BigDecimal totalDeliveryFees = deliveryRepository.sumDeliveryFeeByDriverAndStatus(driver.getId(), DeliveryStatus.DELIVERED);
        BigDecimal collectedCashTotal = paymentRepository.sumCollectedAmountByDriver(driver.getId());
        BigDecimal settledCashTotal = cashSettlementService.settledCashForDriver(driver.getId());
        BigDecimal unsettledCashTotal = cashSettlementService.unsettledCashForDriver(driver.getId());
        return new DriverEarningsResponse(
                completedDeliveries,
                totalDeliveryFees,
                collectedCashTotal,
                settledCashTotal,
                unsettledCashTotal,
                "MVP earnings use delivered delivery fees, collected COD totals, and admin-recorded cash settlements."
        );
    }

    @Transactional
    public DeliveryResponse markArrivedPickup(Authentication authentication, UUID deliveryId) {
        return transitionDriverDelivery(authentication, deliveryId, DeliveryStatus.ARRIVED_AT_PICKUP, null);
    }

    @Transactional
    public DeliveryResponse markPickedUp(Authentication authentication, UUID deliveryId) {
        return transitionDriverDelivery(authentication, deliveryId, DeliveryStatus.PICKED_UP, OrderStatus.PICKED_UP);
    }

    @Transactional
    public DeliveryResponse markOnTheWay(Authentication authentication, UUID deliveryId) {
        return transitionDriverDelivery(authentication, deliveryId, DeliveryStatus.ON_THE_WAY, OrderStatus.ON_THE_WAY);
    }

    @Transactional
    public DeliveryResponse markArrivedDestination(Authentication authentication, UUID deliveryId) {
        return transitionDriverDelivery(authentication, deliveryId, DeliveryStatus.ARRIVED_AT_DESTINATION, null);
    }

    @Transactional
    public DeliveryResponse markDelivered(Authentication authentication, UUID deliveryId) {
        transitionDriverDelivery(authentication, deliveryId, DeliveryStatus.DELIVERED, OrderStatus.DELIVERED);
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        delivery.setDeliveredAt(Instant.now());
        deliveryRepository.save(delivery);
        return toResponse(delivery);
    }

    @Transactional
    public DeliveryResponse markFailed(Authentication authentication, UUID deliveryId, DeliveryFailureRequest request) {
        Driver driver = driverService.currentActiveDriver(authentication);
        Delivery delivery = deliveryRepository.findByIdAndDriver_Id(deliveryId, driver.getId())
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_ACCESS_DENIED", "Delivery does not belong to this driver."));
        deliveryStatusService.requireTransition(delivery.getStatus(), DeliveryStatus.FAILED_DELIVERY);
        delivery.setStatus(DeliveryStatus.FAILED_DELIVERY);
        delivery.setFailureReason(request == null ? null : request.reason());
        delivery = deliveryRepository.save(delivery);
        orderTimelineService.addEvent(delivery.getOrder(), OrderStatus.ISSUE_REPORTED, "Delivery issue reported", delivery.getFailureReason(), driver.getUser());
        return toResponse(delivery);
    }

    private DeliveryResponse transitionDriverDelivery(Authentication authentication, UUID deliveryId, DeliveryStatus nextDeliveryStatus, OrderStatus nextOrderStatus) {
        Driver driver = driverService.currentActiveDriver(authentication);
        Delivery delivery = deliveryRepository.findByIdForUpdate(deliveryId)
                .orElseThrow(() -> new BaseApplicationException("DELIVERY_NOT_FOUND", "Delivery was not found."));
        if (delivery.getDriver() == null || !delivery.getDriver().getId().equals(driver.getId())) {
            throw new BaseApplicationException("DELIVERY_ACCESS_DENIED", "Delivery does not belong to this driver.");
        }
        deliveryStatusService.requireTransition(delivery.getStatus(), nextDeliveryStatus);
        delivery.setStatus(nextDeliveryStatus);
        if (nextDeliveryStatus == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(Instant.now());
        }
        delivery = deliveryRepository.save(delivery);

        if (nextOrderStatus != null) {
            Order order = delivery.getOrder();
            orderStatusService.requireTransition(order.getStatus(), nextOrderStatus);
            order.setStatus(nextOrderStatus);
            orderRepository.save(order);
            orderTimelineService.addEvent(order, nextOrderStatus, titleFor(nextOrderStatus), descriptionFor(nextOrderStatus), driver.getUser());
            notificationService.create(order.getClient().getId(), titleFor(nextOrderStatus), "Order " + order.getOrderNumber() + ": " + descriptionFor(nextOrderStatus));
        }
        return toResponse(delivery);
    }

    public DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getOrder() == null ? null : delivery.getOrder().getId(),
                delivery.getOrder() == null ? null : delivery.getOrder().getOrderNumber(),
                delivery.getOrder() == null || delivery.getOrder().getStore() == null ? null : delivery.getOrder().getStore().getId(),
                delivery.getOrder() == null || delivery.getOrder().getStore() == null ? null : delivery.getOrder().getStore().getName(),
                delivery.getDriver() == null ? null : delivery.getDriver().getId(),
                delivery.getDriver() == null ? null : delivery.getDriver().getUser().getFullName(),
                delivery.getStatus(),
                delivery.getPickupAddressSnapshot(),
                delivery.getDropoffAddressSnapshot(),
                delivery.getDeliveryFee(),
                delivery.getAcceptedAt(),
                delivery.getPickedUpAt(),
                delivery.getDeliveredAt(),
                delivery.getFailureReason(),
                delivery.getCancellationReason(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }

    public DeliverySummaryResponse toSummary(Delivery delivery) {
        return new DeliverySummaryResponse(
                delivery.getId(),
                delivery.getOrder() == null ? null : delivery.getOrder().getId(),
                delivery.getOrder() == null ? null : delivery.getOrder().getOrderNumber(),
                delivery.getOrder() == null || delivery.getOrder().getStore() == null ? null : delivery.getOrder().getStore().getName(),
                delivery.getDriver() == null ? null : delivery.getDriver().getId(),
                delivery.getStatus(),
                delivery.getDeliveryFee(),
                delivery.getPickupAddressSnapshot(),
                delivery.getDropoffAddressSnapshot(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }

    private String titleFor(OrderStatus status) {
        return switch (status) {
            case PICKED_UP -> "Order picked up";
            case ON_THE_WAY -> "Order on the way";
            case DELIVERED -> "Order delivered";
            default -> "Order updated";
        };
    }

    private String descriptionFor(OrderStatus status) {
        return switch (status) {
            case PICKED_UP -> "The driver picked up the order.";
            case ON_THE_WAY -> "The driver is heading to the destination.";
            case DELIVERED -> "The order was delivered.";
            default -> "The order status was updated.";
        };
    }
}

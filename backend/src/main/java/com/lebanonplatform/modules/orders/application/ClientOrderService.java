package com.lebanonplatform.modules.orders.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.AuthorizationService;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.clients.domain.ClientAddress;
import com.lebanonplatform.modules.clients.repository.ClientAddressRepository;
import com.lebanonplatform.modules.inventory.domain.Inventory;
import com.lebanonplatform.modules.inventory.repository.InventoryRepository;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.orders.domain.OrderItem;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import com.lebanonplatform.modules.orders.domain.FulfillmentType;
import com.lebanonplatform.modules.orders.dto.request.CreateOrderItemRequest;
import com.lebanonplatform.modules.orders.dto.request.CreateOrderRequest;
import com.lebanonplatform.modules.orders.dto.request.OrderAddressRequest;
import com.lebanonplatform.modules.orders.dto.response.OrderDetailsResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderItemResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderSummaryResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderTimelineResponse;
import com.lebanonplatform.modules.orders.repository.OrderItemRepository;
import com.lebanonplatform.modules.orders.repository.OrderRepository;
import com.lebanonplatform.modules.payments.domain.PaymentMethod;
import com.lebanonplatform.modules.payments.domain.PaymentStatus;
import com.lebanonplatform.modules.products.domain.Product;
import com.lebanonplatform.modules.products.domain.ProductVariant;
import com.lebanonplatform.modules.products.repository.ProductRepository;
import com.lebanonplatform.modules.products.repository.ProductVariantRepository;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.stores.domain.Store;
import com.lebanonplatform.modules.stores.domain.StoreStatus;
import com.lebanonplatform.modules.stores.application.StoreHoursService;
import com.lebanonplatform.modules.stores.repository.StoreRepository;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final ClientAddressRepository clientAddressRepository;
    private final AuthorizationService authorizationService;
    private final OrderPricingService orderPricingService;
    private final OrderTimelineService orderTimelineService;
    private final OrderStatusService orderStatusService;
    private final NotificationService notificationService;
    private final StoreHoursService storeHoursService;
    private final ObjectMapper objectMapper;

    public ClientOrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            StoreRepository storeRepository,
            ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            InventoryRepository inventoryRepository,
            UserRepository userRepository,
            ClientAddressRepository clientAddressRepository,
            AuthorizationService authorizationService,
            OrderPricingService orderPricingService,
            OrderTimelineService orderTimelineService,
            OrderStatusService orderStatusService,
            NotificationService notificationService,
            StoreHoursService storeHoursService,
            ObjectMapper objectMapper
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.clientAddressRepository = clientAddressRepository;
        this.authorizationService = authorizationService;
        this.orderPricingService = orderPricingService;
        this.orderTimelineService = orderTimelineService;
        this.orderStatusService = orderStatusService;
        this.notificationService = notificationService;
        this.storeHoursService = storeHoursService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderDetailsResponse createOrder(Authentication authentication, CreateOrderRequest request) {
        User client = currentUser(authentication);
        requireClientRole(client.getId());

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new BaseApplicationException("STORE_NOT_FOUND", "Store was not found."));
        if (store.getStatus() != StoreStatus.ACTIVE) {
            throw new BaseApplicationException("STORE_NOT_ACTIVE", "Store is not active.");
        }
        Instant scheduledFor = validateAndResolveScheduledFor(request);
        ensureStoreAcceptsOrderAtRequestedTime(store, request.fulfillmentType(), scheduledFor);

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setClient(client);
        order.setStore(store);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(request.paymentMethod());
        order.setPaymentStatus(request.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY ? PaymentStatus.PENDING_COLLECTION : PaymentStatus.UNPAID);
        order.setFulfillmentType(request.fulfillmentType());
        order.setScheduledFor(scheduledFor);
        order.setNeedsChange(request.needsChange());
        order.setCashAmountClientHas(request.cashAmountClientHas());
        order.setNotes(request.notes());
        order.setAddressSnapshot(writeAddressSnapshot(resolveOrderAddress(client.getId(), request)));

        BigDecimal subtotal = BigDecimal.ZERO;
        order = orderRepository.save(order);
        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = resolveProduct(store.getId(), itemRequest.productId());
            ProductVariant variant = resolveVariant(product, itemRequest.productVariantId());
            ensureInventoryAvailable(product, variant, itemRequest.quantity());

            BigDecimal unitPrice = product.getPrice().add(variant == null ? BigDecimal.ZERO : variant.getPriceAdjustment());
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));
            subtotal = subtotal.add(totalPrice);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setProductVariant(variant);
            item.setProductNameSnapshot(product.getName());
            item.setProductImageSnapshot(product.getImageUrl());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPriceSnapshot(unitPrice);
            item.setTotalPrice(totalPrice);
            orderItemRepository.save(item);
        }

        BigDecimal deliveryFee = orderPricingService.deliveryFee();
        BigDecimal discount = orderPricingService.discount();
        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setDiscount(discount);
        order.setTotal(orderPricingService.total(subtotal, deliveryFee, discount));
        order = orderRepository.save(order);

        String timelineDescription = order.getFulfillmentType() == FulfillmentType.SCHEDULED
                ? "The store received your scheduled order."
                : "The store received your order.";
        orderTimelineService.addEvent(order, OrderStatus.PENDING, "Order received", timelineDescription, client);
        notificationService.create(store.getOwner() == null ? null : store.getOwner().getId(), "New order received", "Order " + order.getOrderNumber() + " is waiting for store review.");
        return toDetails(order);
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> listOrders(Authentication authentication, int page, int size) {
        User client = currentUser(authentication);
        requireClientRole(client.getId());
        return orderRepository.findByClient_IdOrderByCreatedAtDesc(client.getId(), PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getOrder(Authentication authentication, UUID orderId) {
        User client = currentUser(authentication);
        requireClientRole(client.getId());
        return toDetails(orderRepository.findByIdAndClient_Id(orderId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("ORDER_NOT_FOUND", "Order was not found.")));
    }

    @Transactional(readOnly = true)
    public OrderTimelineResponse getTimeline(Authentication authentication, UUID orderId) {
        User client = currentUser(authentication);
        requireClientRole(client.getId());
        orderRepository.findByIdAndClient_Id(orderId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("ORDER_NOT_FOUND", "Order was not found."));
        return orderTimelineService.getTimeline(orderId);
    }

    @Transactional
    public OrderDetailsResponse cancelOrder(Authentication authentication, UUID orderId) {
        User client = currentUser(authentication);
        requireClientRole(client.getId());
        Order order = orderRepository.findByIdAndClient_Id(orderId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("ORDER_NOT_FOUND", "Order was not found."));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BaseApplicationException("CANNOT_CANCEL_ORDER", "Only pending orders can be cancelled.");
        }
        orderStatusService.requireTransition(order.getStatus(), OrderStatus.CANCELLED);
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        orderTimelineService.addEvent(order, OrderStatus.CANCELLED, "Order cancelled", "The order was cancelled by the client.", client);
        notificationService.create(order.getStore().getOwner() == null ? null : order.getStore().getOwner().getId(), "Order cancelled", "Order " + order.getOrderNumber() + " was cancelled by the client.");
        return toDetails(order);
    }

    @Transactional
    public OrderDetailsResponse reorder(Authentication authentication, UUID orderId) {
        User client = currentUser(authentication);
        requireClientRole(client.getId());
        Order sourceOrder = orderRepository.findByIdAndClient_Id(orderId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("ORDER_NOT_FOUND", "Order was not found."));

        List<CreateOrderItemRequest> itemRequests = orderItemRepository.findByOrder_Id(sourceOrder.getId()).stream()
                .map(item -> {
                    if (item.getProduct() == null) {
                        throw new BaseApplicationException("PRODUCT_NOT_AVAILABLE", "One or more products are no longer available.");
                    }
                    return new CreateOrderItemRequest(
                            item.getProduct().getId(),
                            item.getProductVariant() == null ? null : item.getProductVariant().getId(),
                            item.getQuantity()
                    );
                })
                .toList();

        CreateOrderRequest reorderRequest = new CreateOrderRequest(
                sourceOrder.getStore().getId(),
                sourceOrder.getPaymentMethod(),
                FulfillmentType.NOW,
                null,
                sourceOrder.isNeedsChange(),
                sourceOrder.getCashAmountClientHas(),
                "Reorder from " + sourceOrder.getOrderNumber(),
                null,
                readAddressSnapshot(sourceOrder.getAddressSnapshot()),
                itemRequests
        );
        return createOrder(authentication, reorderRequest);
    }

    public OrderDetailsResponse toDetails(Order order) {
        return new OrderDetailsResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getClient().getId(),
                order.getStore().getId(),
                order.getStore().getName(),
                order.getStatus(),
                order.getSubtotal(),
                order.getDeliveryFee(),
                order.getDiscount(),
                order.getTotal(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getFulfillmentType(),
                order.getScheduledFor(),
                order.isNeedsChange(),
                order.getCashAmountClientHas(),
                order.getNotes(),
                order.getAddressSnapshot(),
                orderItemRepository.findByOrder_Id(order.getId()).stream().map(this::toItemResponse).toList(),
                orderTimelineService.listEvents(order.getId()),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public OrderSummaryResponse toSummary(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStore().getId(),
                order.getStore().getName(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getFulfillmentType(),
                order.getScheduledFor(),
                order.getTotal(),
                order.getCreatedAt()
        );
    }

    private Instant validateAndResolveScheduledFor(CreateOrderRequest request) {
        if (request.fulfillmentType() == FulfillmentType.NOW) {
            if (request.scheduledFor() != null) {
                throw new BaseApplicationException("SCHEDULED_TIME_NOT_ALLOWED", "scheduledFor must be empty for immediate orders.");
            }
            return null;
        }

        if (request.fulfillmentType() != FulfillmentType.SCHEDULED) {
            throw new BaseApplicationException("INVALID_FULFILLMENT_TYPE", "Fulfillment type is not supported.");
        }
        if (request.scheduledFor() == null) {
            throw new BaseApplicationException("SCHEDULED_TIME_REQUIRED", "scheduledFor is required for scheduled orders.");
        }

        Instant now = Instant.now();
        Instant minimumScheduledAt = now.plusSeconds(30 * 60);
        Instant maximumScheduledAt = now.plusSeconds(14L * 24 * 60 * 60);
        if (request.scheduledFor().isBefore(minimumScheduledAt)) {
            throw new BaseApplicationException("SCHEDULED_TIME_TOO_SOON", "Scheduled orders must be at least 30 minutes in the future.");
        }
        if (request.scheduledFor().isAfter(maximumScheduledAt)) {
            throw new BaseApplicationException("SCHEDULED_TIME_TOO_FAR", "Scheduled orders can be placed up to 14 days in advance.");
        }
        return request.scheduledFor();
    }

    private void ensureStoreAcceptsOrderAtRequestedTime(Store store, FulfillmentType fulfillmentType, Instant scheduledFor) {
        if (fulfillmentType == FulfillmentType.NOW && !storeHoursService.isOpenNow(store)) {
            throw new BaseApplicationException("STORE_CLOSED", "Store is closed right now. Schedule the order for later.");
        }
        if (fulfillmentType == FulfillmentType.SCHEDULED && scheduledFor != null && !storeHoursService.isOpenAt(store, scheduledFor)) {
            throw new BaseApplicationException("STORE_CLOSED_AT_SCHEDULED_TIME", "Store is closed at the selected scheduled time.");
        }
    }

    private Product resolveProduct(UUID storeId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BaseApplicationException("PRODUCT_NOT_AVAILABLE", "Product is not available."));
        if (!product.getStore().getId().equals(storeId) || !product.isActive() || !product.isAvailable()) {
            throw new BaseApplicationException("PRODUCT_NOT_AVAILABLE", "Product is not available.");
        }
        return product;
    }

    private ProductVariant resolveVariant(Product product, UUID variantId) {
        if (variantId == null) {
            return null;
        }
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BaseApplicationException("PRODUCT_NOT_AVAILABLE", "Product variant is not available."));
        if (!variant.getProduct().getId().equals(product.getId()) || !variant.isAvailable()) {
            throw new BaseApplicationException("PRODUCT_NOT_AVAILABLE", "Product variant is not available.");
        }
        return variant;
    }

    private void ensureInventoryAvailable(Product product, ProductVariant variant, int quantity) {
        Inventory inventory = (variant == null
                ? inventoryRepository.findByProductIdAndProductVariantIsNull(product.getId())
                : inventoryRepository.findByProductIdAndProductVariantId(product.getId(), variant.getId()))
                .orElseThrow(() -> new BaseApplicationException("PRODUCT_OUT_OF_STOCK", "Product is out of stock."));
        if (inventory.getQuantity() - inventory.getReservedQuantity() < quantity) {
            throw new BaseApplicationException("PRODUCT_OUT_OF_STOCK", "Product is out of stock.");
        }
    }

    private OrderAddressRequest resolveOrderAddress(UUID clientUserId, CreateOrderRequest request) {
        if (request.addressId() != null) {
            ClientAddress savedAddress = clientAddressRepository.findByIdAndUser_IdAndActiveTrue(request.addressId(), clientUserId)
                    .orElseThrow(() -> new BaseApplicationException("CLIENT_ADDRESS_NOT_FOUND", "Client address was not found."));
            return new OrderAddressRequest(
                    savedAddress.getLabel(),
                    savedAddress.getFullAddress(),
                    savedAddress.getLatitude(),
                    savedAddress.getLongitude(),
                    savedAddress.getPhoneNumber()
            );
        }
        if (request.address() == null || request.address().fullAddress() == null || request.address().fullAddress().isBlank()) {
            throw new BaseApplicationException("INVALID_ORDER_ADDRESS", "Order address is required.");
        }
        return request.address();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct() == null ? null : item.getProduct().getId(),
                item.getProductVariant() == null ? null : item.getProductVariant().getId(),
                item.getProductNameSnapshot(),
                item.getProductImageSnapshot(),
                item.getQuantity(),
                item.getUnitPriceSnapshot(),
                item.getTotalPrice()
        );
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private void requireClientRole(UUID userId) {
        if (!authorizationService.hasRole(userId, PlatformRole.CLIENT)) {
            throw new BaseApplicationException("ORDER_ACCESS_DENIED", "Client role is required.");
        }
    }

    private String writeAddressSnapshot(Object address) {
        try {
            return objectMapper.writeValueAsString(address);
        } catch (Exception exception) {
            throw new BaseApplicationException("INVALID_ORDER_ADDRESS", "Order address is invalid.");
        }
    }

    private OrderAddressRequest readAddressSnapshot(String addressSnapshot) {
        try {
            return objectMapper.readValue(addressSnapshot, OrderAddressRequest.class);
        } catch (Exception exception) {
            throw new BaseApplicationException("INVALID_ORDER_ADDRESS", "Saved order address is invalid.");
        }
    }

    private String generateOrderNumber() {
        return "ORD-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

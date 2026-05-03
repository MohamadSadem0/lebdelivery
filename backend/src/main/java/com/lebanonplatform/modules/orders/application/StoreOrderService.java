package com.lebanonplatform.modules.orders.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.inventory.domain.Inventory;
import com.lebanonplatform.modules.inventory.domain.InventoryMovement;
import com.lebanonplatform.modules.inventory.domain.InventoryMovementType;
import com.lebanonplatform.modules.inventory.repository.InventoryRepository;
import com.lebanonplatform.modules.notifications.application.NotificationService;
import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.orders.domain.OrderItem;
import com.lebanonplatform.modules.orders.domain.OrderStatus;
import com.lebanonplatform.modules.orders.dto.request.RejectOrderRequest;
import com.lebanonplatform.modules.orders.dto.response.OrderDetailsResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderSummaryResponse;
import com.lebanonplatform.modules.orders.repository.OrderItemRepository;
import com.lebanonplatform.modules.orders.repository.OrderRepository;
import com.lebanonplatform.modules.products.application.ProductService;
import com.lebanonplatform.modules.products.domain.ProductVariant;
import com.lebanonplatform.modules.products.repository.ProductVariantRepository;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final StoreService storeService;
    private final ProductService productService;
    private final com.lebanonplatform.modules.inventory.application.InventoryMovementService inventoryMovementService;
    private final OrderStatusService orderStatusService;
    private final OrderTimelineService orderTimelineService;
    private final ClientOrderService clientOrderService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public StoreOrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InventoryRepository inventoryRepository,
            ProductVariantRepository productVariantRepository,
            StoreService storeService,
            ProductService productService,
            com.lebanonplatform.modules.inventory.application.InventoryMovementService inventoryMovementService,
            OrderStatusService orderStatusService,
            OrderTimelineService orderTimelineService,
            ClientOrderService clientOrderService,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.inventoryRepository = inventoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.storeService = storeService;
        this.productService = productService;
        this.inventoryMovementService = inventoryMovementService;
        this.orderStatusService = orderStatusService;
        this.orderTimelineService = orderTimelineService;
        this.clientOrderService = clientOrderService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> listStoreOrders(Authentication authentication, UUID storeId, int page, int size) {
        storeService.ensureCanManageStore(authentication, storeId);
        return orderRepository.findByStore_IdOrderByCreatedAtDesc(storeId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(clientOrderService::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDetailsResponse getStoreOrder(Authentication authentication, UUID storeId, UUID orderId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return clientOrderService.toDetails(findOrderInStore(storeId, orderId));
    }

    @Transactional
    public OrderDetailsResponse accept(Authentication authentication, UUID storeId, UUID orderId) {
        storeService.ensureCanManageStore(authentication, storeId);
        User actor = currentUser(authentication);
        Order order = findOrderInStore(storeId, orderId);
        orderStatusService.requireTransition(order.getStatus(), OrderStatus.ACCEPTED_BY_STORE);

        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
        for (OrderItem item : items) {
            Inventory inventory = lockInventory(item);
            int newQuantity = inventory.getQuantity() - item.getQuantity();
            if (newQuantity < 0) {
                throw new BaseApplicationException("INSUFFICIENT_INVENTORY", "Inventory is not sufficient for " + item.getProductNameSnapshot() + ".");
            }

            int previousQuantity = inventory.getQuantity();
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);

            ProductVariant variant = item.getProductVariant();
            if (variant != null) {
                variant.setStockQuantity(newQuantity);
                productVariantRepository.save(variant);
            }

            InventoryMovement movement = new InventoryMovement();
            movement.setStore(order.getStore());
            movement.setProduct(item.getProduct());
            movement.setProductVariant(item.getProductVariant());
            movement.setMovementType(InventoryMovementType.ORDER_SALE);
            movement.setQuantityChange(-item.getQuantity());
            movement.setPreviousQuantity(previousQuantity);
            movement.setNewQuantity(newQuantity);
            movement.setReason("Order accepted: " + order.getOrderNumber());
            movement.setReferenceType("ORDER");
            movement.setReferenceId(order.getId());
            movement.setCreatedByUser(actor);
            inventoryMovementService.save(movement);

            productService.updateStockStatus(item.getProduct(), newQuantity, inventory.getLowStockThreshold());
        }

        order.setStatus(OrderStatus.ACCEPTED_BY_STORE);
        order = orderRepository.save(order);
        orderTimelineService.addEvent(order, OrderStatus.ACCEPTED_BY_STORE, "Order accepted", "The store accepted the order.", actor);
        notificationService.create(order.getClient().getId(), "Order accepted", "Order " + order.getOrderNumber() + " was accepted by the store.");
        return clientOrderService.toDetails(order);
    }

    @Transactional
    public OrderDetailsResponse reject(Authentication authentication, UUID storeId, UUID orderId, RejectOrderRequest request) {
        storeService.ensureCanManageStore(authentication, storeId);
        User actor = currentUser(authentication);
        Order order = findOrderInStore(storeId, orderId);
        orderStatusService.requireTransition(order.getStatus(), OrderStatus.REJECTED_BY_STORE);
        order.setStatus(OrderStatus.REJECTED_BY_STORE);
        order = orderRepository.save(order);
        String reason = request == null ? null : request.reason();
        orderTimelineService.addEvent(order, OrderStatus.REJECTED_BY_STORE, "Order rejected", reason == null ? "The store rejected the order." : reason, actor);
        notificationService.create(order.getClient().getId(), "Order rejected", reason == null ? "Order " + order.getOrderNumber() + " was rejected by the store." : reason);
        return clientOrderService.toDetails(order);
    }

    @Transactional
    public OrderDetailsResponse markPreparing(Authentication authentication, UUID storeId, UUID orderId) {
        return transition(authentication, storeId, orderId, OrderStatus.PREPARING, "Preparing", "The store started preparing the order.");
    }

    @Transactional
    public OrderDetailsResponse markReady(Authentication authentication, UUID storeId, UUID orderId) {
        return transition(authentication, storeId, orderId, OrderStatus.READY_FOR_PICKUP, "Ready for pickup", "The order is ready for pickup.");
    }

    private OrderDetailsResponse transition(Authentication authentication, UUID storeId, UUID orderId, OrderStatus next, String title, String description) {
        storeService.ensureCanManageStore(authentication, storeId);
        User actor = currentUser(authentication);
        Order order = findOrderInStore(storeId, orderId);
        orderStatusService.requireTransition(order.getStatus(), next);
        order.setStatus(next);
        order = orderRepository.save(order);
        orderTimelineService.addEvent(order, next, title, description, actor);
        notificationService.create(order.getClient().getId(), title, "Order " + order.getOrderNumber() + ": " + description);
        return clientOrderService.toDetails(order);
    }

    private Inventory lockInventory(OrderItem item) {
        return (item.getProductVariant() == null
                ? inventoryRepository.lockByProductIdAndNoVariant(item.getProduct().getId())
                : inventoryRepository.lockByProductIdAndProductVariantId(item.getProduct().getId(), item.getProductVariant().getId()))
                .orElseThrow(() -> new BaseApplicationException("INSUFFICIENT_INVENTORY", "Inventory is missing for " + item.getProductNameSnapshot() + "."));
    }

    private Order findOrderInStore(UUID storeId, UUID orderId) {
        return orderRepository.findByIdAndStore_Id(orderId, storeId)
                .orElseThrow(() -> new BaseApplicationException("ORDER_DOES_NOT_BELONG_TO_STORE", "Order does not belong to this store."));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }
}

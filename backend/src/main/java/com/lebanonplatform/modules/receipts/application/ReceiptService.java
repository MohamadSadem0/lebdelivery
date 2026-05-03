package com.lebanonplatform.modules.receipts.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.AuthorizationService;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.orders.domain.OrderItem;
import com.lebanonplatform.modules.orders.repository.OrderItemRepository;
import com.lebanonplatform.modules.payments.domain.Payment;
import com.lebanonplatform.modules.receipts.domain.Receipt;
import com.lebanonplatform.modules.receipts.domain.ReceiptType;
import com.lebanonplatform.modules.receipts.dto.response.ReceiptResponse;
import com.lebanonplatform.modules.receipts.repository.ReceiptRepository;
import com.lebanonplatform.modules.roles.domain.PlatformRole;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static java.util.Map.entry;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;
    private final StoreService storeService;
    private final ObjectMapper objectMapper;

    public ReceiptService(
            ReceiptRepository receiptRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository,
            AuthorizationService authorizationService,
            StoreService storeService,
            ObjectMapper objectMapper
    ) {
        this.receiptRepository = receiptRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.authorizationService = authorizationService;
        this.storeService = storeService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReceiptResponse getOrCreateStoreOrderReceipt(Order order, Payment payment) {
        return receiptRepository.findByOrder_Id(order.getId())
                .map(this::toResponse)
                .orElseGet(() -> toResponse(createStoreOrderReceipt(order, payment)));
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> listClientReceipts(Authentication authentication, int page, int size) {
        User client = currentUser(authentication);
        requireRole(client.getId(), PlatformRole.CLIENT);
        return receiptRepository.findByOrder_Client_IdOrderByCreatedAtDesc(
                        client.getId(),
                        PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))
                ).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getClientReceipt(Authentication authentication, UUID receiptId) {
        User client = currentUser(authentication);
        requireRole(client.getId(), PlatformRole.CLIENT);
        return toResponse(receiptRepository.findByIdAndOrder_Client_Id(receiptId, client.getId())
                .orElseThrow(() -> new BaseApplicationException("RECEIPT_NOT_FOUND", "Receipt was not found.")));
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> listStoreReceipts(Authentication authentication, UUID storeId, int page, int size) {
        storeService.ensureCanManageStore(authentication, storeId);
        return receiptRepository.findByOrder_Store_IdOrderByCreatedAtDesc(
                        storeId,
                        PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))
                ).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getStoreReceipt(Authentication authentication, UUID storeId, UUID receiptId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return toResponse(receiptRepository.findByIdAndOrder_Store_Id(receiptId, storeId)
                .orElseThrow(() -> new BaseApplicationException("RECEIPT_NOT_FOUND", "Receipt was not found.")));
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> listAdminReceipts(int page, int size) {
        return receiptRepository.findAll(PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReceiptResponse getAdminReceipt(UUID receiptId) {
        return toResponse(receiptRepository.findById(receiptId)
                .orElseThrow(() -> new BaseApplicationException("RECEIPT_NOT_FOUND", "Receipt was not found.")));
    }

    public ReceiptResponse toResponse(Receipt receipt) {
        Order order = receipt.getOrder();
        Payment payment = receipt.getPayment();
        return new ReceiptResponse(
                receipt.getId(),
                receipt.getReceiptNumber(),
                receipt.getReceiptType(),
                order == null ? null : order.getId(),
                order == null ? null : order.getOrderNumber(),
                order == null || order.getStore() == null ? null : order.getStore().getId(),
                order == null || order.getStore() == null ? null : order.getStore().getName(),
                order == null || order.getClient() == null ? null : order.getClient().getId(),
                order == null || order.getClient() == null ? null : order.getClient().getFullName(),
                payment == null ? null : payment.getId(),
                order == null ? null : order.getSubtotal(),
                order == null ? null : order.getDeliveryFee(),
                order == null ? null : order.getDiscount(),
                receipt.getTotalAmount(),
                payment == null ? null : payment.getMethod(),
                payment == null ? null : payment.getStatus(),
                receipt.getSnapshotJson(),
                receipt.getCreatedAt()
        );
    }

    private Receipt createStoreOrderReceipt(Order order, Payment payment) {
        Receipt receipt = new Receipt();
        receipt.setOrder(order);
        receipt.setPayment(payment);
        receipt.setReceiptType(ReceiptType.STORE_ORDER);
        receipt.setReceiptNumber(generateReceiptNumber());
        receipt.setTotalAmount(order.getTotal());
        receipt.setSnapshotJson(writeSnapshot(order, payment));
        return receiptRepository.save(receipt);
    }

    private String writeSnapshot(Order order, Payment payment) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
        List<Map<String, Object>> itemSnapshots = items.stream()
                .map(item -> Map.<String, Object>of(
                        "productId", item.getProduct() == null ? "" : item.getProduct().getId().toString(),
                        "productVariantId", item.getProductVariant() == null ? "" : item.getProductVariant().getId().toString(),
                        "name", item.getProductNameSnapshot(),
                        "quantity", item.getQuantity(),
                        "unitPrice", item.getUnitPriceSnapshot(),
                        "totalPrice", item.getTotalPrice()
                ))
                .toList();
        Map<String, Object> snapshot = Map.ofEntries(
                entry("receiptType", ReceiptType.STORE_ORDER.name()),
                entry("orderId", order.getId()),
                entry("orderNumber", order.getOrderNumber()),
                entry("storeName", order.getStore().getName()),
                entry("clientName", order.getClient().getFullName()),
                entry("items", itemSnapshots),
                entry("subtotal", order.getSubtotal()),
                entry("deliveryFee", order.getDeliveryFee()),
                entry("discount", order.getDiscount()),
                entry("total", order.getTotal()),
                entry("paymentMethod", payment.getMethod().name()),
                entry("paymentStatus", payment.getStatus().name()),
                entry("generatedAt", Instant.now().toString())
        );
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (Exception exception) {
            throw new BaseApplicationException("RECEIPT_SNAPSHOT_FAILED", "Receipt snapshot could not be generated.");
        }
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BaseApplicationException("UNAUTHENTICATED", "Authentication is required.");
        }
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new BaseApplicationException("USER_NOT_FOUND", "User was not found."));
    }

    private void requireRole(UUID userId, PlatformRole role) {
        if (!authorizationService.hasRole(userId, role)) {
            throw new BaseApplicationException("ACCESS_DENIED", role + " role is required.");
        }
    }

    private String generateReceiptNumber() {
        return "R-" + Instant.now().toEpochMilli() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

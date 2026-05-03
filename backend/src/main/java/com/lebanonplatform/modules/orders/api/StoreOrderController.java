package com.lebanonplatform.modules.orders.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.orders.application.StoreOrderService;
import com.lebanonplatform.modules.orders.dto.request.RejectOrderRequest;
import com.lebanonplatform.modules.orders.dto.response.OrderDetailsResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store-owner/stores/{storeId}/orders")
@PreAuthorize("hasAnyRole('STORE_OWNER','STORE_STAFF','ADMIN')")
public class StoreOrderController {

    private final StoreOrderService storeOrderService;

    public StoreOrderController(StoreOrderService storeOrderService) {
        this.storeOrderService = storeOrderService;
    }

    @GetMapping
    public ApiResponse<List<OrderSummaryResponse>> listOrders(
            Authentication authentication,
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(storeOrderService.listStoreOrders(authentication, storeId, page, size));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailsResponse> getOrder(Authentication authentication, @PathVariable UUID storeId, @PathVariable UUID orderId) {
        return ApiResponse.success(storeOrderService.getStoreOrder(authentication, storeId, orderId));
    }

    @PostMapping("/{orderId}/accept")
    public ApiResponse<OrderDetailsResponse> accept(Authentication authentication, @PathVariable UUID storeId, @PathVariable UUID orderId) {
        return ApiResponse.success("Order accepted.", storeOrderService.accept(authentication, storeId, orderId));
    }

    @PostMapping("/{orderId}/reject")
    public ApiResponse<OrderDetailsResponse> reject(
            Authentication authentication,
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @RequestBody(required = false) RejectOrderRequest request
    ) {
        return ApiResponse.success("Order rejected.", storeOrderService.reject(authentication, storeId, orderId, request));
    }

    @PostMapping("/{orderId}/mark-preparing")
    public ApiResponse<OrderDetailsResponse> markPreparing(Authentication authentication, @PathVariable UUID storeId, @PathVariable UUID orderId) {
        return ApiResponse.success("Order marked preparing.", storeOrderService.markPreparing(authentication, storeId, orderId));
    }

    @PostMapping("/{orderId}/mark-ready")
    public ApiResponse<OrderDetailsResponse> markReady(Authentication authentication, @PathVariable UUID storeId, @PathVariable UUID orderId) {
        return ApiResponse.success("Order marked ready.", storeOrderService.markReady(authentication, storeId, orderId));
    }
}

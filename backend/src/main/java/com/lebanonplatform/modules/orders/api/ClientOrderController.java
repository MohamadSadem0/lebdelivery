package com.lebanonplatform.modules.orders.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.orders.application.ClientOrderService;
import com.lebanonplatform.modules.orders.dto.request.CreateOrderRequest;
import com.lebanonplatform.modules.orders.dto.response.OrderDetailsResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderSummaryResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderTimelineResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/client/orders")
@PreAuthorize("hasRole('CLIENT')")
public class ClientOrderController {

    private final ClientOrderService clientOrderService;

    public ClientOrderController(ClientOrderService clientOrderService) {
        this.clientOrderService = clientOrderService;
    }

    @PostMapping
    public ApiResponse<OrderDetailsResponse> createOrder(Authentication authentication, @Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success("Order created.", clientOrderService.createOrder(authentication, request));
    }

    @GetMapping
    public ApiResponse<List<OrderSummaryResponse>> listOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(clientOrderService.listOrders(authentication, page, size));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderDetailsResponse> getOrder(Authentication authentication, @PathVariable UUID orderId) {
        return ApiResponse.success(clientOrderService.getOrder(authentication, orderId));
    }

    @GetMapping("/{orderId}/timeline")
    public ApiResponse<OrderTimelineResponse> getTimeline(Authentication authentication, @PathVariable UUID orderId) {
        return ApiResponse.success(clientOrderService.getTimeline(authentication, orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderDetailsResponse> cancelOrder(Authentication authentication, @PathVariable UUID orderId) {
        return ApiResponse.success("Order cancelled.", clientOrderService.cancelOrder(authentication, orderId));
    }

    @PostMapping("/{orderId}/reorder")
    public ApiResponse<OrderDetailsResponse> reorder(Authentication authentication, @PathVariable UUID orderId) {
        return ApiResponse.success("Reorder created.", clientOrderService.reorder(authentication, orderId));
    }
}

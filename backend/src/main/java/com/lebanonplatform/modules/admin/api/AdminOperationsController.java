package com.lebanonplatform.modules.admin.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.admin.application.AdminOperationsService;
import com.lebanonplatform.modules.deliveries.dto.response.DeliveryResponse;
import com.lebanonplatform.modules.deliveries.dto.response.DeliverySummaryResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderDetailsResponse;
import com.lebanonplatform.modules.orders.dto.response.OrderSummaryResponse;
import com.lebanonplatform.modules.payments.dto.request.CreateCashSettlementRequest;
import com.lebanonplatform.modules.payments.dto.response.CashSettlementResponse;
import com.lebanonplatform.modules.payments.dto.response.PaymentResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOperationsController {

    private final AdminOperationsService adminOperationsService;

    public AdminOperationsController(AdminOperationsService adminOperationsService) {
        this.adminOperationsService = adminOperationsService;
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderSummaryResponse>> listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminOperationsService.listOrders(page, size));
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderDetailsResponse> getOrder(@PathVariable UUID orderId) {
        return ApiResponse.success(adminOperationsService.getOrder(orderId));
    }

    @GetMapping("/deliveries")
    public ApiResponse<List<DeliverySummaryResponse>> listDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminOperationsService.listDeliveries(page, size));
    }

    @GetMapping("/deliveries/{deliveryId}")
    public ApiResponse<DeliveryResponse> getDelivery(@PathVariable UUID deliveryId) {
        return ApiResponse.success(adminOperationsService.getDelivery(deliveryId));
    }

    @GetMapping("/payments")
    public ApiResponse<List<PaymentResponse>> listPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminOperationsService.listPayments(page, size));
    }

    @GetMapping("/payments/{paymentId}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable UUID paymentId) {
        return ApiResponse.success(adminOperationsService.getPayment(paymentId));
    }

    @GetMapping("/cash-settlements")
    public ApiResponse<List<CashSettlementResponse>> listCashSettlements(
            @RequestParam(required = false) UUID driverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(adminOperationsService.listCashSettlements(driverId, page, size));
    }

    @PostMapping("/cash-settlements")
    public ApiResponse<CashSettlementResponse> createCashSettlement(
            Authentication authentication,
            @Valid @RequestBody CreateCashSettlementRequest request
    ) {
        return ApiResponse.success("Cash settlement recorded.", adminOperationsService.createCashSettlement(authentication, request));
    }
}

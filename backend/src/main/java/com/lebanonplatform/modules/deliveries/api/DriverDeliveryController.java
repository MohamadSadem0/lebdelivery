package com.lebanonplatform.modules.deliveries.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.deliveries.application.DeliveryService;
import com.lebanonplatform.modules.deliveries.dto.request.DeliveryFailureRequest;
import com.lebanonplatform.modules.deliveries.dto.response.DeliveryResponse;
import com.lebanonplatform.modules.deliveries.dto.response.DeliverySummaryResponse;
import com.lebanonplatform.modules.deliveries.dto.response.DriverEarningsResponse;
import com.lebanonplatform.modules.payments.application.PaymentCollectionService;
import com.lebanonplatform.modules.payments.dto.request.CashCollectionRequest;
import com.lebanonplatform.modules.payments.dto.response.CashCollectionResponse;
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
@RequestMapping("/api/v1/driver")
@PreAuthorize("hasAnyRole('INDEPENDENT_DRIVER','STORE_DRIVER','ADMIN')")
public class DriverDeliveryController {

    private final DeliveryService deliveryService;
    private final PaymentCollectionService paymentCollectionService;

    public DriverDeliveryController(DeliveryService deliveryService, PaymentCollectionService paymentCollectionService) {
        this.deliveryService = deliveryService;
        this.paymentCollectionService = paymentCollectionService;
    }

    @GetMapping("/jobs/available")
    public ApiResponse<List<DeliverySummaryResponse>> availableJobs(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(deliveryService.listAvailableJobs(authentication, page, size));
    }

    @GetMapping("/jobs/{deliveryId}")
    public ApiResponse<DeliveryResponse> getJob(Authentication authentication, @PathVariable UUID deliveryId) {
        return ApiResponse.success(deliveryService.getDriverJob(authentication, deliveryId));
    }

    @PostMapping("/jobs/{deliveryId}/accept")
    public ApiResponse<DeliveryResponse> acceptJob(Authentication authentication, @PathVariable UUID deliveryId) {
        return ApiResponse.success("Delivery accepted.", deliveryService.acceptJob(authentication, deliveryId));
    }

    @GetMapping("/deliveries/active")
    public ApiResponse<List<DeliverySummaryResponse>> activeDeliveries(Authentication authentication) {
        return ApiResponse.success(deliveryService.listActiveDriverDeliveries(authentication));
    }

    @GetMapping("/deliveries/history")
    public ApiResponse<List<DeliverySummaryResponse>> deliveryHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(deliveryService.listDriverHistory(authentication, page, size));
    }

    @GetMapping("/earnings")
    public ApiResponse<DriverEarningsResponse> earnings(Authentication authentication) {
        return ApiResponse.success(deliveryService.getDriverEarnings(authentication));
    }

    @PostMapping("/deliveries/{deliveryId}/arrived-pickup")
    public ApiResponse<DeliveryResponse> arrivedPickup(Authentication authentication, @PathVariable UUID deliveryId) {
        return ApiResponse.success(deliveryService.markArrivedPickup(authentication, deliveryId));
    }

    @PostMapping("/deliveries/{deliveryId}/picked-up")
    public ApiResponse<DeliveryResponse> pickedUp(Authentication authentication, @PathVariable UUID deliveryId) {
        return ApiResponse.success(deliveryService.markPickedUp(authentication, deliveryId));
    }

    @PostMapping("/deliveries/{deliveryId}/on-the-way")
    public ApiResponse<DeliveryResponse> onTheWay(Authentication authentication, @PathVariable UUID deliveryId) {
        return ApiResponse.success(deliveryService.markOnTheWay(authentication, deliveryId));
    }

    @PostMapping("/deliveries/{deliveryId}/arrived-destination")
    public ApiResponse<DeliveryResponse> arrivedDestination(Authentication authentication, @PathVariable UUID deliveryId) {
        return ApiResponse.success(deliveryService.markArrivedDestination(authentication, deliveryId));
    }

    @PostMapping("/deliveries/{deliveryId}/delivered")
    public ApiResponse<DeliveryResponse> delivered(Authentication authentication, @PathVariable UUID deliveryId) {
        return ApiResponse.success(deliveryService.markDelivered(authentication, deliveryId));
    }

    @PostMapping("/deliveries/{deliveryId}/failed")
    public ApiResponse<DeliveryResponse> failed(Authentication authentication, @PathVariable UUID deliveryId, @RequestBody(required = false) DeliveryFailureRequest request) {
        return ApiResponse.success(deliveryService.markFailed(authentication, deliveryId, request));
    }

    @PostMapping("/deliveries/{deliveryId}/cash-collected")
    public ApiResponse<CashCollectionResponse> cashCollected(
            Authentication authentication,
            @PathVariable UUID deliveryId,
            @Valid @RequestBody CashCollectionRequest request
    ) {
        return ApiResponse.success("Cash collection recorded.", paymentCollectionService.markCashCollected(authentication, deliveryId, request));
    }
}

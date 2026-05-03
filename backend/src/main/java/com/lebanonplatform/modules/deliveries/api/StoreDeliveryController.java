package com.lebanonplatform.modules.deliveries.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.deliveries.application.DeliveryService;
import com.lebanonplatform.modules.deliveries.dto.request.RequestDeliveryRequest;
import com.lebanonplatform.modules.deliveries.dto.response.DeliveryResponse;
import com.lebanonplatform.modules.deliveries.dto.response.DeliverySummaryResponse;
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
@RequestMapping("/api/v1/store-owner/stores/{storeId}")
@PreAuthorize("hasAnyRole('STORE_OWNER','STORE_STAFF','ADMIN')")
public class StoreDeliveryController {

    private final DeliveryService deliveryService;

    public StoreDeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/orders/{orderId}/delivery-request")
    public ApiResponse<DeliveryResponse> requestDelivery(
            Authentication authentication,
            @PathVariable UUID storeId,
            @PathVariable UUID orderId,
            @Valid @RequestBody(required = false) RequestDeliveryRequest request
    ) {
        return ApiResponse.success("Delivery requested.", deliveryService.requestDelivery(authentication, storeId, orderId, request));
    }

    @GetMapping("/deliveries")
    public ApiResponse<List<DeliverySummaryResponse>> listDeliveries(
            Authentication authentication,
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(deliveryService.listStoreDeliveries(authentication, storeId, page, size));
    }

    @GetMapping("/deliveries/{deliveryId}")
    public ApiResponse<DeliveryResponse> getDelivery(Authentication authentication, @PathVariable UUID storeId, @PathVariable UUID deliveryId) {
        return ApiResponse.success(deliveryService.getStoreDelivery(authentication, storeId, deliveryId));
    }
}

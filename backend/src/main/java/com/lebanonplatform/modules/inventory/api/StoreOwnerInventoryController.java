package com.lebanonplatform.modules.inventory.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.inventory.application.InventoryService;
import com.lebanonplatform.modules.inventory.dto.request.InventoryAdjustmentRequest;
import com.lebanonplatform.modules.inventory.dto.response.InventoryMovementResponse;
import com.lebanonplatform.modules.inventory.dto.response.InventoryResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store-owner/stores/{storeId}")
@PreAuthorize("hasAnyRole('STORE_OWNER','STORE_STAFF','ADMIN')")
public class StoreOwnerInventoryController {

    private final InventoryService inventoryService;

    public StoreOwnerInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/inventory")
    public ApiResponse<List<InventoryResponse>> listInventory(Authentication authentication, @PathVariable UUID storeId) {
        return ApiResponse.success(inventoryService.listInventory(authentication, storeId));
    }

    @GetMapping("/inventory/low-stock")
    public ApiResponse<List<InventoryResponse>> listLowStock(Authentication authentication, @PathVariable UUID storeId) {
        return ApiResponse.success(inventoryService.listLowStock(authentication, storeId));
    }

    @GetMapping("/inventory/out-of-stock")
    public ApiResponse<List<InventoryResponse>> listOutOfStock(Authentication authentication, @PathVariable UUID storeId) {
        return ApiResponse.success(inventoryService.listOutOfStock(authentication, storeId));
    }

    @PostMapping("/inventory/{productId}/adjust")
    public ApiResponse<InventoryResponse> adjustInventory(
            Authentication authentication,
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @Valid @RequestBody InventoryAdjustmentRequest request
    ) {
        return ApiResponse.success("Inventory adjusted.", inventoryService.adjustInventory(authentication, storeId, productId, request));
    }

    @GetMapping("/inventory-movements")
    public ApiResponse<List<InventoryMovementResponse>> listInventoryMovements(Authentication authentication, @PathVariable UUID storeId) {
        return ApiResponse.success(inventoryService.listMovements(authentication, storeId));
    }
}

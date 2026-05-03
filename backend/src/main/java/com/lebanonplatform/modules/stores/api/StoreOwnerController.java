package com.lebanonplatform.modules.stores.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.stores.application.StoreTypeConfigService;
import com.lebanonplatform.modules.stores.dto.request.CreateStoreRequest;
import com.lebanonplatform.modules.stores.dto.request.UpdateStoreRequest;
import com.lebanonplatform.modules.stores.dto.response.ProductFormConfigResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreSummaryResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store-owner/stores")
public class StoreOwnerController {

    private final StoreService storeService;
    private final StoreTypeConfigService storeTypeConfigService;

    public StoreOwnerController(StoreService storeService, StoreTypeConfigService storeTypeConfigService) {
        this.storeService = storeService;
        this.storeTypeConfigService = storeTypeConfigService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENT','STORE_OWNER','ADMIN')")
    public ApiResponse<StoreResponse> createStore(Authentication authentication, @Valid @RequestBody CreateStoreRequest request) {
        return ApiResponse.success("Store created.", storeService.createStore(authentication, request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<StoreSummaryResponse>> listOwnedStores(Authentication authentication) {
        return ApiResponse.success(storeService.listOwnedStores(authentication));
    }

    @GetMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('STORE_OWNER','STORE_STAFF','ADMIN')")
    public ApiResponse<StoreResponse> getOwnedStore(Authentication authentication, @PathVariable UUID storeId) {
        return ApiResponse.success(storeService.getOwnedStore(authentication, storeId));
    }

    @GetMapping("/{storeId}/product-form-config")
    @PreAuthorize("hasAnyRole('STORE_OWNER','STORE_STAFF','ADMIN')")
    public ApiResponse<ProductFormConfigResponse> getProductFormConfig(Authentication authentication, @PathVariable UUID storeId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return ApiResponse.success(storeTypeConfigService.getProductFormConfig(storeId));
    }

    @PatchMapping("/{storeId}")
    @PreAuthorize("hasAnyRole('STORE_OWNER','STORE_STAFF','ADMIN')")
    public ApiResponse<StoreResponse> updateStore(
            Authentication authentication,
            @PathVariable UUID storeId,
            @Valid @RequestBody UpdateStoreRequest request
    ) {
        return ApiResponse.success("Store updated.", storeService.updateStore(authentication, storeId, request));
    }
}

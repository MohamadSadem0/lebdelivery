package com.lebanonplatform.modules.stores.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.stores.application.StoreTypeConfigService;
import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import com.lebanonplatform.modules.stores.dto.response.ProductFormConfigResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreTypeConfigResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreTypeSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class StoreTypeController {

    private final StoreTypeConfigService storeTypeConfigService;

    public StoreTypeController(StoreTypeConfigService storeTypeConfigService) {
        this.storeTypeConfigService = storeTypeConfigService;
    }

    @GetMapping("/store-types")
    public ApiResponse<List<StoreTypeSummaryResponse>> getStoreTypes() {
        return ApiResponse.success(storeTypeConfigService.getStoreTypes());
    }

    @GetMapping("/store-types/{code}/config")
    public ApiResponse<StoreTypeConfigResponse> getStoreTypeConfig(@PathVariable StoreTypeCode code) {
        return ApiResponse.success(storeTypeConfigService.getConfig(code));
    }

    @GetMapping("/stores/{storeId}/product-form-config")
    public ApiResponse<ProductFormConfigResponse> getProductFormConfig(@PathVariable UUID storeId) {
        return ApiResponse.success(storeTypeConfigService.getProductFormConfig(storeId));
    }
}

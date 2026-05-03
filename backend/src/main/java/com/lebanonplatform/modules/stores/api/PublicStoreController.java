package com.lebanonplatform.modules.stores.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.products.application.ProductService;
import com.lebanonplatform.modules.products.dto.response.ProductResponse;
import com.lebanonplatform.modules.products.dto.response.ProductSummaryResponse;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.stores.dto.response.StoreResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreSummaryResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
public class PublicStoreController {

    private final StoreService storeService;
    private final ProductService productService;

    public PublicStoreController(StoreService storeService, ProductService productService) {
        this.storeService = storeService;
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<StoreSummaryResponse>> listStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(storeService.listPublicStores(page, size));
    }

    @GetMapping("/{storeId}")
    public ApiResponse<StoreResponse> getStore(@PathVariable UUID storeId) {
        return ApiResponse.success(storeService.getPublicStore(storeId));
    }

    @GetMapping("/{storeId}/products")
    public ApiResponse<List<ProductSummaryResponse>> listProducts(
            @PathVariable UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(productService.listPublicProducts(storeId, page, size));
    }

    @GetMapping("/{storeId}/products/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable UUID storeId, @PathVariable UUID productId) {
        return ApiResponse.success(productService.getPublicProduct(storeId, productId));
    }
}

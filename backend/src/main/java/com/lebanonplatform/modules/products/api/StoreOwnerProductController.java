package com.lebanonplatform.modules.products.api;

import com.lebanonplatform.common.response.ApiResponse;
import com.lebanonplatform.modules.products.application.ProductService;
import com.lebanonplatform.modules.products.dto.request.CreateProductRequest;
import com.lebanonplatform.modules.products.dto.request.UpdateProductAvailabilityRequest;
import com.lebanonplatform.modules.products.dto.request.UpdateProductRequest;
import com.lebanonplatform.modules.products.dto.response.ProductResponse;
import com.lebanonplatform.modules.products.dto.response.ProductSummaryResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/store-owner/stores/{storeId}/products")
@PreAuthorize("hasAnyRole('STORE_OWNER','STORE_STAFF','ADMIN')")
public class StoreOwnerProductController {

    private final ProductService productService;

    public StoreOwnerProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductSummaryResponse>> listProducts(
            Authentication authentication,
            @PathVariable UUID storeId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.success(productService.listOwnerProducts(authentication, storeId, page, size));
    }

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(
            Authentication authentication,
            @PathVariable UUID storeId,
            @Valid @RequestBody CreateProductRequest request
    ) {
        return ApiResponse.success("Product created.", productService.createProduct(authentication, storeId, request));
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(Authentication authentication, @PathVariable UUID storeId, @PathVariable UUID productId) {
        return ApiResponse.success(productService.getOwnerProduct(authentication, storeId, productId));
    }

    @PatchMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            Authentication authentication,
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return ApiResponse.success("Product updated.", productService.updateProduct(authentication, storeId, productId, request));
    }

    @PatchMapping("/{productId}/availability")
    public ApiResponse<ProductResponse> updateAvailability(
            Authentication authentication,
            @PathVariable UUID storeId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductAvailabilityRequest request
    ) {
        return ApiResponse.success("Product availability updated.", productService.updateAvailability(authentication, storeId, productId, request.available()));
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> deleteProduct(Authentication authentication, @PathVariable UUID storeId, @PathVariable UUID productId) {
        productService.deleteProduct(authentication, storeId, productId);
        return ApiResponse.<Void>success("Product deleted.", null);
    }
}

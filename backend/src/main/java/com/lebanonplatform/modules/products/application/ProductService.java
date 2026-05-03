package com.lebanonplatform.modules.products.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.inventory.application.InventoryService;
import com.lebanonplatform.modules.inventory.domain.InventoryMode;
import com.lebanonplatform.modules.products.domain.Product;
import com.lebanonplatform.modules.products.domain.ProductAttribute;
import com.lebanonplatform.modules.products.domain.ProductVariant;
import com.lebanonplatform.modules.products.domain.StockStatus;
import com.lebanonplatform.modules.products.dto.request.CreateProductRequest;
import com.lebanonplatform.modules.products.dto.request.ProductAttributeRequest;
import com.lebanonplatform.modules.products.dto.request.ProductVariantRequest;
import com.lebanonplatform.modules.products.dto.request.UpdateProductRequest;
import com.lebanonplatform.modules.products.dto.response.ProductAttributeResponse;
import com.lebanonplatform.modules.products.dto.response.ProductResponse;
import com.lebanonplatform.modules.products.dto.response.ProductSummaryResponse;
import com.lebanonplatform.modules.products.dto.response.ProductVariantResponse;
import com.lebanonplatform.modules.products.repository.ProductAttributeRepository;
import com.lebanonplatform.modules.products.repository.ProductRepository;
import com.lebanonplatform.modules.products.repository.ProductVariantRepository;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.stores.domain.Store;
import com.lebanonplatform.modules.stores.domain.StoreStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final TypeReference<Map<String, String>> STRING_MAP = new TypeReference<>() {
    };

    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final StoreService storeService;
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public ProductService(
            ProductRepository productRepository,
            ProductAttributeRepository productAttributeRepository,
            ProductVariantRepository productVariantRepository,
            StoreService storeService,
            @Lazy InventoryService inventoryService,
            ObjectMapper objectMapper
    ) {
        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.productVariantRepository = productVariantRepository;
        this.storeService = storeService;
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> listOwnerProducts(Authentication authentication, UUID storeId, int page, int size) {
        storeService.ensureCanManageStore(authentication, storeId);
        return productRepository.findByStoreIdOrderByCreatedAtDesc(storeId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(Authentication authentication, UUID storeId, CreateProductRequest request) {
        storeService.ensureCanManageStore(authentication, storeId);
        Store store = storeService.findStore(storeId);

        Product product = new Product();
        product.setStore(store);
        product.setCategoryId(request.categoryId());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setImageUrl(request.imageUrl());
        product.setAvailable(request.available() == null || request.available());
        product.setActive(true);
        product.setStockStatus(StockStatus.USUALLY_AVAILABLE);
        product = productRepository.save(product);

        saveAttributes(product, request.attributes());
        List<ProductVariant> variants = saveVariants(product, request.variants());
        Product savedProduct = product;

        if (request.initialInventory() != null && variants.isEmpty()) {
            inventoryService.createInitialInventory(authentication, store, savedProduct, request.initialInventory());
        }

        variants.forEach(variant -> inventoryService.createInitialVariantInventory(
                authentication,
                store,
                savedProduct,
                variant,
                variant.getStockQuantity(),
                request.initialInventory() == null ? 0 : request.initialInventory().lowStockThreshold(),
                request.initialInventory() == null ? null : request.initialInventory().unitType()
        ));

        return toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getOwnerProduct(Authentication authentication, UUID storeId, UUID productId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return toResponse(findProductInStore(storeId, productId));
    }

    @Transactional
    public ProductResponse updateProduct(Authentication authentication, UUID storeId, UUID productId, UpdateProductRequest request) {
        storeService.ensureCanManageStore(authentication, storeId);
        Product product = findProductInStore(storeId, productId);

        if (request.categoryId() != null) {
            product.setCategoryId(request.categoryId());
        }
        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.imageUrl() != null) {
            product.setImageUrl(request.imageUrl());
        }
        if (request.available() != null) {
            product.setAvailable(request.available());
        }
        if (request.attributes() != null) {
            productAttributeRepository.deleteByProductId(product.getId());
            saveAttributes(product, request.attributes());
        }

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateAvailability(Authentication authentication, UUID storeId, UUID productId, boolean available) {
        storeService.ensureCanManageStore(authentication, storeId);
        Product product = findProductInStore(storeId, productId);
        product.setAvailable(available);
        if (!available) {
            product.setStockStatus(StockStatus.UNAVAILABLE);
        }
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Authentication authentication, UUID storeId, UUID productId) {
        storeService.ensureCanManageStore(authentication, storeId);
        Product product = findProductInStore(storeId, productId);
        product.setActive(false);
        product.setAvailable(false);
        product.setStockStatus(StockStatus.UNAVAILABLE);
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> listPublicProducts(UUID storeId, int page, int size) {
        Store store = storeService.findStore(storeId);
        if (store.getStatus() != StoreStatus.ACTIVE) {
            throw new BaseApplicationException("STORE_NOT_FOUND", "Store was not found.");
        }
        return productRepository.findByStoreIdAndActiveTrueAndAvailableTrueOrderByCreatedAtDesc(storeId, PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 50))).stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getPublicProduct(UUID storeId, UUID productId) {
        Store store = storeService.findStore(storeId);
        if (store.getStatus() != StoreStatus.ACTIVE) {
            throw new BaseApplicationException("STORE_NOT_FOUND", "Store was not found.");
        }
        Product product = findProductInStore(storeId, productId);
        if (!product.isActive() || !product.isAvailable()) {
            throw new BaseApplicationException("PRODUCT_NOT_FOUND", "Product was not found.");
        }
        return toResponse(product);
    }

    public Product findProductInStore(UUID storeId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BaseApplicationException("PRODUCT_NOT_FOUND", "Product was not found."));
        if (!product.getStore().getId().equals(storeId)) {
            throw new BaseApplicationException("PRODUCT_NOT_FOUND", "Product was not found in this store.");
        }
        return product;
    }

    public void updateStockStatus(Product product, int quantity, int lowStockThreshold) {
        if (!product.isAvailable()) {
            product.setStockStatus(StockStatus.UNAVAILABLE);
        } else if (quantity <= 0) {
            product.setStockStatus(StockStatus.OUT_OF_STOCK);
        } else if (lowStockThreshold > 0 && quantity <= Math.max(1, lowStockThreshold / 2)) {
            product.setStockStatus(StockStatus.ONLY_FEW_LEFT);
        } else if (lowStockThreshold > 0 && quantity <= lowStockThreshold) {
            product.setStockStatus(StockStatus.LOW_STOCK);
        } else {
            product.setStockStatus(StockStatus.IN_STOCK);
        }
        productRepository.save(product);
    }

    public ProductSummaryResponse toSummary(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getStore().getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.isAvailable(),
                product.getStockStatus()
        );
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getStore().getId(),
                product.getCategoryId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.isAvailable(),
                product.getStockStatus(),
                productAttributeRepository.findByProductIdOrderByKeyAsc(product.getId()).stream().map(this::toAttributeResponse).toList(),
                productVariantRepository.findByProductIdOrderByNameAsc(product.getId()).stream().map(this::toVariantResponse).toList(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private void saveAttributes(Product product, List<ProductAttributeRequest> attributes) {
        if (attributes == null) {
            return;
        }
        attributes.forEach(request -> {
            ProductAttribute attribute = new ProductAttribute();
            attribute.setProduct(product);
            attribute.setKey(request.key());
            attribute.setValue(request.value());
            productAttributeRepository.save(attribute);
        });
    }

    private List<ProductVariant> saveVariants(Product product, List<ProductVariantRequest> variants) {
        if (variants == null) {
            return List.of();
        }
        return variants.stream().map(request -> {
            ProductVariant variant = new ProductVariant();
            variant.setProduct(product);
            variant.setName(request.name());
            variant.setSku(request.sku());
            variant.setPriceAdjustment(request.priceAdjustment() == null ? BigDecimal.ZERO : request.priceAdjustment());
            variant.setStockQuantity(request.stockQuantity() == null ? 0 : request.stockQuantity());
            variant.setAttributesJson(writeStringMap(request.attributes()));
            variant.setAvailable(request.available() == null || request.available());
            return productVariantRepository.save(variant);
        }).toList();
    }

    private ProductAttributeResponse toAttributeResponse(ProductAttribute attribute) {
        return new ProductAttributeResponse(attribute.getId(), attribute.getKey(), attribute.getValue());
    }

    private ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getName(),
                variant.getSku(),
                variant.getPriceAdjustment(),
                variant.getStockQuantity(),
                readStringMap(variant.getAttributesJson()),
                variant.isAvailable()
        );
    }

    private String writeStringMap(Map<String, String> value) {
        if (value == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new BaseApplicationException("INVALID_VARIANT_ATTRIBUTES", "Variant attributes are invalid.");
        }
    }

    private Map<String, String> readStringMap(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(json, STRING_MAP);
        } catch (Exception exception) {
            return Map.of();
        }
    }
}

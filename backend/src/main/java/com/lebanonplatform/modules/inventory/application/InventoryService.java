package com.lebanonplatform.modules.inventory.application;

import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.common.security.UserPrincipal;
import com.lebanonplatform.modules.inventory.domain.Inventory;
import com.lebanonplatform.modules.inventory.domain.InventoryMode;
import com.lebanonplatform.modules.inventory.domain.InventoryMovement;
import com.lebanonplatform.modules.inventory.domain.InventoryMovementType;
import com.lebanonplatform.modules.inventory.dto.request.InventoryAdjustmentRequest;
import com.lebanonplatform.modules.inventory.dto.response.InventoryMovementResponse;
import com.lebanonplatform.modules.inventory.dto.response.InventoryResponse;
import com.lebanonplatform.modules.inventory.repository.InventoryRepository;
import com.lebanonplatform.modules.products.application.ProductService;
import com.lebanonplatform.modules.products.domain.Product;
import com.lebanonplatform.modules.products.domain.ProductVariant;
import com.lebanonplatform.modules.products.dto.request.InitialInventoryRequest;
import com.lebanonplatform.modules.products.repository.ProductVariantRepository;
import com.lebanonplatform.modules.stores.application.StoreService;
import com.lebanonplatform.modules.stores.domain.Store;
import com.lebanonplatform.modules.users.domain.User;
import com.lebanonplatform.modules.users.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final StoreService storeService;
    private final ProductService productService;
    private final InventoryMovementService inventoryMovementService;
    private final UserRepository userRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductVariantRepository productVariantRepository,
            StoreService storeService,
            @Lazy ProductService productService,
            InventoryMovementService inventoryMovementService,
            UserRepository userRepository
    ) {
        this.inventoryRepository = inventoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.storeService = storeService;
        this.productService = productService;
        this.inventoryMovementService = inventoryMovementService;
        this.userRepository = userRepository;
    }

    @Transactional
    public InventoryResponse createInitialInventory(Authentication authentication, Store store, Product product, InitialInventoryRequest request) {
        if (request.quantity() < 0) {
            throw new BaseApplicationException("INVALID_INVENTORY_QUANTITY", "Inventory quantity cannot be negative.");
        }

        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setInventoryMode(resolveInventoryMode(store));
        inventory.setQuantity(request.quantity());
        inventory.setReservedQuantity(0);
        inventory.setLowStockThreshold(request.lowStockThreshold());
        inventory.setUnitType(request.unitType());
        inventory = inventoryRepository.save(inventory);

        if (request.quantity() != 0) {
            createMovement(authentication, inventory, InventoryMovementType.STOCK_ADDED, request.quantity(), 0, request.quantity(), "Initial inventory", null, null);
        }
        productService.updateStockStatus(product, inventory.getQuantity(), inventory.getLowStockThreshold());
        return toResponse(inventory);
    }

    @Transactional
    public InventoryResponse createInitialVariantInventory(
            Authentication authentication,
            Store store,
            Product product,
            ProductVariant variant,
            int quantity,
            int lowStockThreshold,
            com.lebanonplatform.modules.inventory.domain.UnitType unitType
    ) {
        if (quantity < 0) {
            throw new BaseApplicationException("INVALID_INVENTORY_QUANTITY", "Inventory quantity cannot be negative.");
        }

        Inventory inventory = new Inventory();
        inventory.setStore(store);
        inventory.setProduct(product);
        inventory.setProductVariant(variant);
        inventory.setInventoryMode(InventoryMode.VARIANT_BASED);
        inventory.setQuantity(quantity);
        inventory.setReservedQuantity(0);
        inventory.setLowStockThreshold(lowStockThreshold);
        inventory.setUnitType(unitType);
        inventory = inventoryRepository.save(inventory);

        if (quantity != 0) {
            createMovement(authentication, inventory, InventoryMovementType.STOCK_ADDED, quantity, 0, quantity, "Initial variant inventory", null, null);
        }
        return toResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listInventory(Authentication authentication, UUID storeId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return inventoryRepository.findByStoreIdOrderByUpdatedAtDesc(storeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listLowStock(Authentication authentication, UUID storeId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return inventoryRepository.findByStoreIdOrderByUpdatedAtDesc(storeId).stream()
                .filter(inventory -> inventory.getQuantity() > 0 && inventory.getLowStockThreshold() > 0)
                .filter(inventory -> inventory.getQuantity() <= inventory.getLowStockThreshold())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listOutOfStock(Authentication authentication, UUID storeId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return inventoryRepository.findByStoreIdAndQuantityLessThanEqualOrderByUpdatedAtDesc(storeId, 0).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public InventoryResponse adjustInventory(Authentication authentication, UUID storeId, UUID productId, InventoryAdjustmentRequest request) {
        storeService.ensureCanManageStore(authentication, storeId);
        if (request.quantityChange() == 0) {
            throw new BaseApplicationException("INVALID_INVENTORY_ADJUSTMENT", "Inventory adjustment quantity cannot be zero.");
        }
        Product product = productService.findProductInStore(storeId, productId);
        ProductVariant variant = resolveVariant(product, request.productVariantId());
        Inventory inventory = resolveInventory(storeService.findStore(storeId), product, variant);

        int previousQuantity = inventory.getQuantity();
        int newQuantity = previousQuantity + request.quantityChange();
        if (newQuantity < 0) {
            throw new BaseApplicationException("INVENTORY_CANNOT_GO_BELOW_ZERO", "Inventory quantity cannot go below zero.");
        }

        inventory.setQuantity(newQuantity);
        inventory = inventoryRepository.save(inventory);

        if (variant != null) {
            variant.setStockQuantity(newQuantity);
            productVariantRepository.save(variant);
        }

        createMovement(authentication, inventory, movementTypeFor(request.quantityChange()), request.quantityChange(), previousQuantity, newQuantity, request.reason(), "MANUAL", null);
        productService.updateStockStatus(product, newQuantity, inventory.getLowStockThreshold());
        return toResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryMovementResponse> listMovements(Authentication authentication, UUID storeId) {
        storeService.ensureCanManageStore(authentication, storeId);
        return inventoryMovementService.listForStore(storeId);
    }

    private Inventory resolveInventory(Store store, Product product, ProductVariant variant) {
        return (variant == null
                ? inventoryRepository.findByProductIdAndProductVariantIsNull(product.getId())
                : inventoryRepository.findByProductIdAndProductVariantId(product.getId(), variant.getId()))
                .orElseGet(() -> {
                    Inventory inventory = new Inventory();
                    inventory.setStore(store);
                    inventory.setProduct(product);
                    inventory.setProductVariant(variant);
                    inventory.setInventoryMode(resolveInventoryMode(store));
                    inventory.setQuantity(0);
                    inventory.setReservedQuantity(0);
                    inventory.setLowStockThreshold(0);
                    return inventoryRepository.save(inventory);
                });
    }

    private ProductVariant resolveVariant(Product product, UUID variantId) {
        if (variantId == null) {
            return null;
        }
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new BaseApplicationException("PRODUCT_VARIANT_NOT_FOUND", "Product variant was not found."));
        if (!variant.getProduct().getId().equals(product.getId())) {
            throw new BaseApplicationException("PRODUCT_VARIANT_NOT_FOUND", "Product variant was not found for this product.");
        }
        return variant;
    }

    private InventoryMode resolveInventoryMode(Store store) {
        return switch (store.getStoreTypeCode()) {
            case RESTAURANT, BAKERY -> InventoryMode.AVAILABILITY_BASED;
            case CLOTHING -> InventoryMode.VARIANT_BASED;
            default -> InventoryMode.QUANTITY_BASED;
        };
    }

    private InventoryMovementType movementTypeFor(int quantityChange) {
        return quantityChange >= 0 ? InventoryMovementType.STOCK_ADDED : InventoryMovementType.STOCK_REMOVED;
    }

    private void createMovement(
            Authentication authentication,
            Inventory inventory,
            InventoryMovementType movementType,
            int quantityChange,
            int previousQuantity,
            int newQuantity,
            String reason,
            String referenceType,
            UUID referenceId
    ) {
        InventoryMovement movement = new InventoryMovement();
        movement.setStore(inventory.getStore());
        movement.setProduct(inventory.getProduct());
        movement.setProductVariant(inventory.getProductVariant());
        movement.setMovementType(movementType);
        movement.setQuantityChange(quantityChange);
        movement.setPreviousQuantity(previousQuantity);
        movement.setNewQuantity(newQuantity);
        movement.setReason(reason);
        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);
        movement.setCreatedByUser(currentUserOrNull(authentication));
        inventoryMovementService.save(movement);
    }

    private User currentUserOrNull(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return null;
        }
        return userRepository.findById(principal.getId()).orElse(null);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getStore().getId(),
                inventory.getProduct().getId(),
                inventory.getProductVariant() == null ? null : inventory.getProductVariant().getId(),
                inventory.getInventoryMode(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                inventory.getLowStockThreshold(),
                inventory.getUnitType(),
                inventory.getUpdatedAt()
        );
    }
}

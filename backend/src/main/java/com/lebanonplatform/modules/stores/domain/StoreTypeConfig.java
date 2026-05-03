package com.lebanonplatform.modules.stores.domain;

import com.lebanonplatform.common.audit.BaseEntity;
import com.lebanonplatform.modules.inventory.domain.InventoryMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "store_type_configs")
public class StoreTypeConfig extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 40)
    private StoreTypeCode storeTypeCode;

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private InventoryMode inventoryMode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String enabledFeaturesJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String requiredProductFieldsJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionalProductFieldsJson;

    @Column(nullable = false)
    private boolean supportsModifiers;

    @Column(nullable = false)
    private boolean supportsVariants;

    @Column(nullable = false)
    private boolean supportsExpiryDate;

    @Column(nullable = false)
    private boolean supportsWeightItems;

    @Column(nullable = false)
    private boolean supportsPreparationTime;

    @Column(nullable = false)
    private boolean supportsPrescriptionFlag;

    public StoreTypeCode getStoreTypeCode() {
        return storeTypeCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public InventoryMode getInventoryMode() {
        return inventoryMode;
    }

    public String getEnabledFeaturesJson() {
        return enabledFeaturesJson;
    }

    public String getRequiredProductFieldsJson() {
        return requiredProductFieldsJson;
    }

    public String getOptionalProductFieldsJson() {
        return optionalProductFieldsJson;
    }

    public boolean isSupportsModifiers() {
        return supportsModifiers;
    }

    public boolean isSupportsVariants() {
        return supportsVariants;
    }

    public boolean isSupportsExpiryDate() {
        return supportsExpiryDate;
    }

    public boolean isSupportsWeightItems() {
        return supportsWeightItems;
    }

    public boolean isSupportsPreparationTime() {
        return supportsPreparationTime;
    }

    public boolean isSupportsPrescriptionFlag() {
        return supportsPrescriptionFlag;
    }
}

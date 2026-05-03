package com.lebanonplatform.modules.stores.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lebanonplatform.common.exception.BaseApplicationException;
import com.lebanonplatform.modules.stores.domain.Store;
import com.lebanonplatform.modules.stores.domain.StoreTypeCode;
import com.lebanonplatform.modules.stores.domain.StoreTypeConfig;
import com.lebanonplatform.modules.stores.dto.response.ProductFormConfigResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreTypeConfigResponse;
import com.lebanonplatform.modules.stores.dto.response.StoreTypeSummaryResponse;
import com.lebanonplatform.modules.stores.repository.StoreRepository;
import com.lebanonplatform.modules.stores.repository.StoreTypeConfigRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StoreTypeConfigService {

    private static final TypeReference<List<String>> STRING_LIST = new TypeReference<>() {
    };

    private final StoreTypeConfigRepository storeTypeConfigRepository;
    private final StoreRepository storeRepository;
    private final ObjectMapper objectMapper;

    public StoreTypeConfigService(
            StoreTypeConfigRepository storeTypeConfigRepository,
            StoreRepository storeRepository,
            ObjectMapper objectMapper
    ) {
        this.storeTypeConfigRepository = storeTypeConfigRepository;
        this.storeRepository = storeRepository;
        this.objectMapper = objectMapper;
    }

    public List<StoreTypeSummaryResponse> getStoreTypes() {
        return storeTypeConfigRepository.findAllByOrderByDisplayNameAsc().stream()
                .map(config -> new StoreTypeSummaryResponse(config.getStoreTypeCode(), config.getDisplayName()))
                .toList();
    }

    public StoreTypeConfigResponse getConfig(StoreTypeCode code) {
        StoreTypeConfig config = storeTypeConfigRepository.findByStoreTypeCode(code)
                .orElseThrow(() -> new BaseApplicationException("STORE_TYPE_CONFIG_NOT_FOUND", "Store type config was not found."));

        return toResponse(config);
    }

    public ProductFormConfigResponse getProductFormConfig(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BaseApplicationException("STORE_NOT_FOUND", "Store was not found."));
        StoreTypeConfigResponse config = getConfig(store.getStoreTypeCode());
        return new ProductFormConfigResponse(
                storeId,
                config.storeTypeCode(),
                config.inventoryMode(),
                config.supportsVariants(),
                config.supportsExpiryDate(),
                config.supportsWeightItems(),
                config.supportsModifiers(),
                config.supportsPreparationTime(),
                config.supportsPrescriptionFlag(),
                config.enabledFeatures(),
                config.requiredProductFields(),
                config.optionalProductFields()
        );
    }

    private StoreTypeConfigResponse toResponse(StoreTypeConfig config) {
        return new StoreTypeConfigResponse(
                config.getStoreTypeCode(),
                config.getDisplayName(),
                config.getInventoryMode(),
                readStringList(config.getEnabledFeaturesJson()),
                readStringList(config.getRequiredProductFieldsJson()),
                readStringList(config.getOptionalProductFieldsJson()),
                config.isSupportsModifiers(),
                config.isSupportsVariants(),
                config.isSupportsExpiryDate(),
                config.isSupportsWeightItems(),
                config.isSupportsPreparationTime(),
                config.isSupportsPrescriptionFlag()
        );
    }

    private List<String> readStringList(String json) {
        try {
            return objectMapper.readValue(json, STRING_LIST);
        } catch (Exception exception) {
            throw new BaseApplicationException("STORE_TYPE_CONFIG_INVALID", "Store type config contains invalid JSON.");
        }
    }
}

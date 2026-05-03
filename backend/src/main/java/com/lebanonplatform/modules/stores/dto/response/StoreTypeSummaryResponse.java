package com.lebanonplatform.modules.stores.dto.response;

import com.lebanonplatform.modules.stores.domain.StoreTypeCode;

public record StoreTypeSummaryResponse(
        StoreTypeCode code,
        String displayName
) {
}

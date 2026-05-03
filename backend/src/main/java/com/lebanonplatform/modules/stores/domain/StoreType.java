package com.lebanonplatform.modules.stores.domain;

import com.lebanonplatform.common.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "store_types")
public class StoreType extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 40)
    private StoreTypeCode code;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private boolean active = true;
}

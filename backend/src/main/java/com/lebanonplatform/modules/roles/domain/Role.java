package com.lebanonplatform.modules.roles.domain;

import com.lebanonplatform.common.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Column
    private String description;
}

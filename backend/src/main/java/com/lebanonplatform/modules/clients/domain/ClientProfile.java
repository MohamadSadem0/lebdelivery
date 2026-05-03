package com.lebanonplatform.modules.clients.domain;

import com.lebanonplatform.common.audit.BaseEntity;
import com.lebanonplatform.modules.users.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "client_profiles")
public class ClientProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column
    private String defaultCity;

    @Column(columnDefinition = "TEXT")
    private String defaultAddressSnapshot;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDefaultCity() {
        return defaultCity;
    }

    public void setDefaultCity(String defaultCity) {
        this.defaultCity = defaultCity;
    }

    public String getDefaultAddressSnapshot() {
        return defaultAddressSnapshot;
    }

    public void setDefaultAddressSnapshot(String defaultAddressSnapshot) {
        this.defaultAddressSnapshot = defaultAddressSnapshot;
    }
}

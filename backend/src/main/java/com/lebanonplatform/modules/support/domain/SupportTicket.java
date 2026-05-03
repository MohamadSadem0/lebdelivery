package com.lebanonplatform.modules.support.domain;

import com.lebanonplatform.common.audit.BaseEntity;
import com.lebanonplatform.modules.deliveries.domain.Delivery;
import com.lebanonplatform.modules.orders.domain.Order;
import com.lebanonplatform.modules.servicerequests.domain.ServiceRequest;
import com.lebanonplatform.modules.users.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "support_tickets")
public class SupportTicket extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SupportTicketStatus status = SupportTicketStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_order_id")
    private Order relatedOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_delivery_id")
    private Delivery relatedDelivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_service_request_id")
    private ServiceRequest relatedServiceRequest;

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SupportTicketStatus getStatus() {
        return status;
    }

    public void setStatus(SupportTicketStatus status) {
        this.status = status;
    }

    public Order getRelatedOrder() {
        return relatedOrder;
    }

    public void setRelatedOrder(Order relatedOrder) {
        this.relatedOrder = relatedOrder;
    }

    public Delivery getRelatedDelivery() {
        return relatedDelivery;
    }

    public void setRelatedDelivery(Delivery relatedDelivery) {
        this.relatedDelivery = relatedDelivery;
    }

    public ServiceRequest getRelatedServiceRequest() {
        return relatedServiceRequest;
    }

    public void setRelatedServiceRequest(ServiceRequest relatedServiceRequest) {
        this.relatedServiceRequest = relatedServiceRequest;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}
